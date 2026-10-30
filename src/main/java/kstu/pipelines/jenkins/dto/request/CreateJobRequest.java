package kstu.pipelines.jenkins.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateJobRequest(
        @NotBlank(message = "Job name must not be blank")
        @Size(max = 100, message = "Job name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Repository URL must not be blank")
        @Size(max = 500, message = "Repository URL must not exceed 500 characters")
        String repositoryUrl,

        @NotBlank(message = "Branch must not be blank")
        @Size(max = 255, message = "Branch must not exceed 255 characters")
        String branch,

        @Positive(message = "SCM polling interval must be positive")
        Integer pollScmIntervalMinutes,

        @Positive(message = "Periodic build interval must be positive")
        Integer periodicBuildIntervalMinutes
) {
}
