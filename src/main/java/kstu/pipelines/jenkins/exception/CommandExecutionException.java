package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class CommandExecutionException extends BusinessException {
    public CommandExecutionException(String message) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                cause
        );
    }
}
