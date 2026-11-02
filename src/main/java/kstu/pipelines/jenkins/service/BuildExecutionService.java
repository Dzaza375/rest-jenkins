package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.dto.execution.BuildExecutionContext;
import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.dto.execution.WorkspaceResult;
import kstu.pipelines.jenkins.enums.BuildStatus;
import kstu.pipelines.jenkins.enums.StepStatus;
import kstu.pipelines.jenkins.service.factory.BuildStepExecutorCreator;
import kstu.pipelines.jenkins.service.factory.BuildStepExecutorCreatorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class BuildExecutionService {
    private final BuildService buildService;
    private final GitService gitService;
    private final BuildStepExecutorCreatorRegistry creatorRegistry;

    @Async
    public void prepareBuild(Long buildId) {
        try {
            BuildExecutionContext context = buildService.getExecutionContext(buildId);

            WorkspaceResult workspace = gitService.prepareWorkspace(
                    context.jobId(),
                    context.buildNumber(),
                    context.repositoryUrl(),
                    context.branch()
            );

            buildService.updateCommitHash(buildId, workspace.commitHash());

            executeBuildSteps(context, workspace.path());


        } catch (RuntimeException e) {
            buildService.finishExecution(buildId, BuildStatus.FAILURE);
            throw e;
        }
    }

    private void executeBuildSteps(BuildExecutionContext context, Path workspace) {
        buildService.startExecution(context.buildId());

        for (BuildStepExecutionContext stepContext : context.steps()) {
            buildService.startStepExecution(stepContext.executionId());

            try {
                BuildStepExecutorCreator creator = creatorRegistry.getCreator(stepContext.type());
                String output = creator.executeStep(stepContext, workspace);
                buildService.appendStepConsoleOutput(stepContext.executionId(), output);
                buildService.appendBuildConsoleOutput(context.buildId(), output);

                buildService.finishStepExecution(stepContext.executionId(), StepStatus.SUCCESS);
            } catch (RuntimeException e) {
                String output = e.getMessage();

                if (output != null) {
                    buildService.appendStepConsoleOutput(stepContext.executionId(), output);
                    buildService.appendBuildConsoleOutput(context.buildId(), output);
                }

                buildService.finishStepExecution(stepContext.executionId(), StepStatus.FAILURE);
                throw e;
            }
        }

        buildService.finishExecution(context.buildId(), BuildStatus.SUCCESS);
    }
}
