package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class BuildNotFoundException extends BusinessException {
    public BuildNotFoundException(Long id) {
        super(
                "Build with id " + id + " not found",
                HttpStatus.NOT_FOUND
        );
    }
}
