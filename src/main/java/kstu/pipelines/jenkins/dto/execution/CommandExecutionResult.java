package kstu.pipelines.jenkins.dto.execution;

public record CommandExecutionResult(
        String output,
        int exitCode,
        boolean timedOut
) {
}
