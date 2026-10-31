package kstu.pipelines.jenkins.dto.response;

import kstu.pipelines.jenkins.enums.BuildStatus;

public record BuildConsoleResponse(
        Long buildId,
        Integer buildNumber,
        BuildStatus status,
        String consoleOutput
) {
}
