package kstu.pipelines.jenkins.service.scheduler;

import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.service.BuildService;
import kstu.pipelines.jenkins.service.GitService;
import kstu.pipelines.jenkins.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PollScmScheduler {
    private final JobService jobService;
    private final GitService gitService;
    private final BuildService buildService;

    @Scheduled(fixedDelay = 60_000)
    public void pollScm() {
        LocalDateTime now = LocalDateTime.now();

        for (Job job : jobService.getJobsForScmPolling()) {
            if (!isPollingDue(job, now)) {
                continue;
            }

            try {
                String remoteCommitHash = gitService.getRemoteCommitHash(
                        job.getRepositoryUrl(),
                        job.getBranch()
                );

                String lastCommitHash = job.getLastCommitHash();

                if (lastCommitHash == null) {
                    jobService.updateScmPollingState(job.getId(), remoteCommitHash, now);
                } else if (!lastCommitHash.equals(remoteCommitHash)) {
                    buildService.startBuild(job.getId());

                    jobService.updateScmPollingState(job.getId(), remoteCommitHash, now);

                    log.info("Changes detected for job '{}'. Build started.", job.getName());
                } else {
                    jobService.updateScmPollingState(job.getId(), lastCommitHash, now);
                }
            } catch (RuntimeException e) {
                log.error("Failed to poll SCM for job '{}'", job.getName(), e);
            }
        }
    }

    private boolean isPollingDue(Job job, LocalDateTime now) {
        return job.getLastScmPollAt() == null
                || !job.getLastScmPollAt()
                .plusMinutes(job.getPollScmIntervalMinutes())
                .isAfter(now);
    }
}
