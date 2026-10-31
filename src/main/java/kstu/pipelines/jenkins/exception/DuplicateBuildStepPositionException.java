package kstu.pipelines.jenkins.exception;

import org.springframework.http.HttpStatus;

public class DuplicateBuildStepPositionException extends BusinessException {
    public DuplicateBuildStepPositionException(Long jobId, Integer position) {
        super(
                "BuildStep with position " + position
                + " in Job with id " + jobId + " already exists",
                HttpStatus.CONFLICT
        );
    }
}
