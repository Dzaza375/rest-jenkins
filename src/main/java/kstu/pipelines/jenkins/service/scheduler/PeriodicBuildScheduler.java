package kstu.pipelines.jenkins.service.scheduler;

import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.service.BuildService;
import kstu.pipelines.jenkins.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PeriodicBuildScheduler {
    private final JobService jobService;
    private final BuildService buildService;

    @Scheduled(fixedDelay = 60_000)
    public void periodicBuild() {
        LocalDateTime now = LocalDateTime.now();

        for (Job job : jobService.getJobsForPeriodicBuild()) {
            if (!isPeriodDue(job, now)) {
                continue;
            }

            try {
                buildService.startBuild(job.getId());
                jobService.updateLastPeriodicBuildAt(job.getId(), now);
            } catch (RuntimeException e) {
                log.error("Failed to build periodic for job '{}'", job.getName(), e);
            }
        }
    }

    private boolean isPeriodDue(Job job, LocalDateTime now) {
        return job.getLastPeriodicBuildAt() == null
                || !job.getLastPeriodicBuildAt()
                .plusMinutes(job.getPeriodicBuildIntervalMinutes())
                .isAfter(now);
    }
}
