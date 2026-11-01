package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

import java.nio.file.Path;

public class WorkspaceCreationException extends BusinessException {
    public WorkspaceCreationException(Path path, Throwable cause) {
        super(
                "Failed to create workspace directory: " + path,
                HttpStatus.INTERNAL_SERVER_ERROR,
                cause
        );
    }
}
