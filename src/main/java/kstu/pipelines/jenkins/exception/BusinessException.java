package kstu.pipelines.jenkins.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message, HttpStatus status, Throwable cause) {
        super(message);
        this.status = status;
        initCause(cause);
    }
}
