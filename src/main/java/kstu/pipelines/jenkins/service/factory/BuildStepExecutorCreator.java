package kstu.pipelines.jenkins.service.factory;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.enums.StepType;
import kstu.pipelines.jenkins.service.execution.BuildStepExecutor;

import java.nio.file.Path;

public abstract class BuildStepExecutorCreator {
    public String executeStep(BuildStepExecutionContext context, Path workspace) {
        BuildStepExecutor executor = createExecutor();
        return executor.execute(context, workspace);
    }

    protected abstract BuildStepExecutor createExecutor();

    public abstract StepType getSupportedType();
}
