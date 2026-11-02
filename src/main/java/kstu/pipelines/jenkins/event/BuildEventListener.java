package kstu.pipelines.jenkins.event;

import kstu.pipelines.jenkins.dto.BuildCreatedEvent;
import kstu.pipelines.jenkins.service.BuildExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BuildEventListener {
    private final BuildExecutionService buildExecutionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBuildCreated(BuildCreatedEvent event) {
        buildExecutionService.prepareBuild(event.buildId());
    }
}
