package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class BuildStepNotFoundException extends BusinessException {
    public BuildStepNotFoundException(Long id) {
        super(
                "BuildStep with id " + id + " not found",
                HttpStatus.NOT_FOUND
        );
    }

    public BuildStepNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND
        );
    }
}
