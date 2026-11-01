package kstu.pipelines.jenkins.dto.execution;

import java.nio.file.Path;

public record WorkspaceResult(
        Path path,
        String commitHash
) {
}
