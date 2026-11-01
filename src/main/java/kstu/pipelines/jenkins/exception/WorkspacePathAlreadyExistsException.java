package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

import java.nio.file.Path;

public class WorkspacePathAlreadyExistsException extends BusinessException {
    public WorkspacePathAlreadyExistsException(Path path) {
        super(
                "Workspace with path " + path + " already exists",
                HttpStatus.CONFLICT
        );
    }
}
