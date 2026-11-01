package kstu.pipelines.jenkins.dto.execution;

import java.util.List;

public record BuildExecutionContext(
        Long buildId,
        Long jobId,
        Integer buildNumber,
        String repositoryUrl,
        String branch,
        List<BuildStepExecutionContext> steps
) {
}
