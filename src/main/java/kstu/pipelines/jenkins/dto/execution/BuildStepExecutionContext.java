package kstu.pipelines.jenkins.dto.execution;

import kstu.pipelines.jenkins.enums.StepType;

public record BuildStepExecutionContext(
        Long executionId,
        String name,
        StepType type,
        String command,
        Integer position
) {
}
