package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;

import java.nio.file.Path;

public class ShellBuildStepExecutor implements BuildStepExecutor {
    @Override
    public String execute(BuildStepExecutionContext context, Path workspace) {
        //TODO do shell command
    }
}
