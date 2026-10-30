package kstu.pipelines.jenkins.dto.response;

import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String name,
        String repositoryUrl,
        String branch,
        boolean enabled,
        String lastCommitHash,
        Integer pollScmIntervalMinutes,
        Integer periodicBuildIntervalMinutes,
        LocalDateTime createdAt
) {
}
