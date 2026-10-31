package kstu.pipelines.jenkins.dto.response;

import kstu.pipelines.jenkins.enums.StepType;

public record BuildStepResponse(
        Long id,
        String name,
        StepType type,
        String command,
        Integer position
) {
}
