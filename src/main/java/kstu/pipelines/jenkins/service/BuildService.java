package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.dto.response.BuildConsoleResponse;
import kstu.pipelines.jenkins.dto.response.BuildResponse;
import kstu.pipelines.jenkins.entity.Build;
import kstu.pipelines.jenkins.entity.BuildStep;
import kstu.pipelines.jenkins.entity.BuildStepExecution;
import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.enums.BuildStatus;
import kstu.pipelines.jenkins.exception.BuildNotFoundException;
import kstu.pipelines.jenkins.exception.JobNotFoundException;
import kstu.pipelines.jenkins.mapper.BuildMapper;
import kstu.pipelines.jenkins.repository.BuildRepository;
import kstu.pipelines.jenkins.repository.BuildStepRepository;
import kstu.pipelines.jenkins.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildService {
    private final BuildRepository buildRepository;
    private final JobRepository jobRepository;
    private final BuildStepRepository buildStepRepository;
    private final BuildMapper buildMapper;

    @Transactional
    public BuildResponse startBuild(Long jobId) {
        Job job = findJobById(jobId);

        Integer buildNumber = buildRepository
                .findTopByJobIdOrderByBuildNumberDesc(jobId)
                .map(build -> build.getBuildNumber() + 1)
                .orElse(1);

        List<BuildStep> buildSteps = buildStepRepository.findByJobIdOrderByPositionAsc(jobId);

        Build build = Build.builder()
                .job(job)
                .buildNumber(buildNumber)
                .status(BuildStatus.QUEUED)
                .commitHash(null)
                .build();

        for (BuildStep step : buildSteps) {
            build.addBuildStepExecution(
                    BuildStepExecution.builder()
                            .name(step.getName())
                            .type(step.getType())
                            .command(step.getCommand())
                            .position(step.getPosition())
                            .build()
            );
        }

        return buildMapper.toResponse(buildRepository.save(build));
    }

    @Transactional(readOnly = true)
    public List<BuildResponse> getAllBuilds(Long jobId) {
        findJobById(jobId);

        return buildRepository.findByJobIdOrderByBuildNumberDesc(jobId)
                .stream()
                .map(buildMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BuildResponse getBuild(Long jobId, Long buildId) {
        findJobById(jobId);

        Build build = buildRepository.findByIdAndJobId(buildId, jobId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));

        return buildMapper.toResponse(build);
    }

    @Transactional(readOnly = true)
    public BuildConsoleResponse getBuildConsole(Long buildId) {
        Build build = buildRepository.findById(buildId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));

        return buildMapper.toConsoleResponse(build);
    }

    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }
}
