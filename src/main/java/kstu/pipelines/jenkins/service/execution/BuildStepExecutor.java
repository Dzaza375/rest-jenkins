package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;

import java.nio.file.Path;

public interface BuildStepExecutor {
    void execute(BuildStepExecutionContext context, Path workspace);
}
