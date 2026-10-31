package kstu.pipelines.jenkins.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kstu.pipelines.jenkins.enums.StepType;

public record UpdateBuildStepRequest(
        @NotBlank(message = "Step name must not be blank")
        @Size(max = 100, message = "Step name must not exceed 100 characters")
        String name,

        @NotNull(message = "Step type must not be null")
        StepType type,

        @NotBlank(message = "Command must not be blank")
        @Size(max = 2000, message = "Command must not exceed 2000 characters")
        String command,

        @NotNull(message = "Step position must not be null")
        @Positive(message = "Step position must be positive")
        Integer position
) {
}
