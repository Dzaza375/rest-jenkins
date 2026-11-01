package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class GitCommandException extends BusinessException {
    public GitCommandException(String message) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public GitCommandException(String message, Throwable cause) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                cause
        );
    }
}
