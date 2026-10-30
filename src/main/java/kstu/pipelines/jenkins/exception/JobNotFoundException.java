package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class JobNotFoundException extends BusinessException {
    public JobNotFoundException(Long id) {
        super(
                "Job with id " + id  + " not found",
                HttpStatus.NOT_FOUND
        );
    }
}
