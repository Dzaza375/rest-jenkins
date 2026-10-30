package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class DuplicateJobNameException extends BusinessException {
    public DuplicateJobNameException(String name) {
        super(
                "Job with name " + name + " already exists",
                HttpStatus.CONFLICT
        );
    }
}
