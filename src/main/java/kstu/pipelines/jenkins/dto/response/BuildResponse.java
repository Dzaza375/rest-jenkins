package kstu.pipelines.jenkins.dto.response;

import kstu.pipelines.jenkins.enums.BuildStatus;

import java.time.LocalDateTime;

public record BuildResponse(
        Long id,
        Long jobId,
        Integer buildNumber,
        BuildStatus status,
        String commitHash,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
}
