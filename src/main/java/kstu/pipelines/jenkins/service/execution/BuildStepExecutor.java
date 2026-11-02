package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;

import java.nio.file.Path;

public interface BuildStepExecutor {
    String execute(BuildStepExecutionContext context, Path workspace);
}
