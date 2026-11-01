package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class BuildStepExecutionNotFoundExection extends BusinessException {
    public BuildStepExecutionNotFoundExection(Long id) {
        super(
                "BuildStepExecution with id " + id + " not found",
                HttpStatus.NOT_FOUND
        );
    }
}
