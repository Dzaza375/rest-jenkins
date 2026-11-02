package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.dto.BuildCreatedEvent;
import kstu.pipelines.jenkins.dto.execution.BuildExecutionContext;
import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.dto.response.BuildConsoleResponse;
import kstu.pipelines.jenkins.dto.response.BuildResponse;
import kstu.pipelines.jenkins.entity.Build;
import kstu.pipelines.jenkins.entity.BuildStep;
import kstu.pipelines.jenkins.entity.BuildStepExecution;
import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.enums.BuildStatus;
import kstu.pipelines.jenkins.enums.StepStatus;
import kstu.pipelines.jenkins.exception.BuildNotFoundException;
import kstu.pipelines.jenkins.exception.BuildStepNotFoundException;
import kstu.pipelines.jenkins.exception.JobNotFoundException;
import kstu.pipelines.jenkins.mapper.BuildMapper;
import kstu.pipelines.jenkins.repository.BuildRepository;
import kstu.pipelines.jenkins.repository.BuildStepExecutionRepository;
import kstu.pipelines.jenkins.repository.BuildStepRepository;
import kstu.pipelines.jenkins.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildService {
    private final BuildRepository buildRepository;
    private final JobRepository jobRepository;
    private final BuildStepRepository buildStepRepository;
    private final BuildStepExecutionRepository buildStepExecutionRepository;
    private final BuildMapper buildMapper;
    private final ApplicationEventPublisher eventPublisher;

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

        Build savedBuild = buildRepository.save(build);
        eventPublisher.publishEvent(new BuildCreatedEvent(savedBuild.getId()));
        return buildMapper.toResponse(savedBuild);
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
        Build build = findBuildById(buildId);

        return buildMapper.toConsoleResponse(build);
    }

    @Transactional(readOnly = true)
    public BuildExecutionContext getExecutionContext(Long buildId) {
        Build build = findBuildById(buildId);
        Job job = build.getJob();

        return new BuildExecutionContext(
                build.getId(),
                job.getId(),
                build.getBuildNumber(),
                job.getRepositoryUrl(),
                job.getBranch(),
                build.getBuildStepExecutions()
                        .stream()
                        .sorted(Comparator.comparing(BuildStepExecution::getPosition))
                        .map(step -> new BuildStepExecutionContext(
                                step.getId(),
                                step.getName(),
                                step.getType(),
                                step.getCommand(),
                                step.getPosition()
                        ))
                        .toList()
        );
    }

    @Transactional
    public void updateCommitHash(Long buildId, String commitHash) {
        Build build = findBuildById(buildId);
        build.setCommitHash(commitHash);
    }

    @Transactional
    public void updateStatus(Long buildId, BuildStatus status) {
        Build build = findBuildById(buildId);
        build.setStatus(status);
    }

    @Transactional
    public void startExecution(Long buildId) {
        Build build = findBuildById(buildId);
        build.setStatus(BuildStatus.RUNNING);
        build.setStartedAt(LocalDateTime.now());
    }

    @Transactional
    public void finishExecution(Long buildId, BuildStatus status) {
        Build build = findBuildById(buildId);
        build.setStatus(status);
        build.setFinishedAt(LocalDateTime.now());
    }

    @Transactional
    public void startStepExecution(Long executionId) {
        BuildStepExecution execution = findBuildStepExecutionById(executionId);
        execution.setStatus(StepStatus.RUNNING);
    }

    @Transactional
    public void finishStepExecution(Long executionId, StepStatus stepStatus) {
        if (stepStatus != StepStatus.SUCCESS && stepStatus != StepStatus.FAILURE) {
            throw new IllegalArgumentException("Invalid terminal step status");
        }

        BuildStepExecution execution = findBuildStepExecutionById(executionId);
        execution.setStatus(stepStatus);
    }

    @Transactional
    public void appendStepConsoleOutput(Long executionId, String output) {
        BuildStepExecution execution = findBuildStepExecutionById(executionId);
        execution.setConsoleOutput(execution.getConsoleOutput() + output);
    }

    @Transactional
    public void appendBuildConsoleOutput(Long buildId, String output) {
        Build build = findBuildById(buildId);
        build.setConsoleOutput(build.getConsoleOutput() + output);
    }

    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    private Build findBuildById(Long buildId) {
        return buildRepository.findById(buildId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));
    }

    private BuildStepExecution findBuildStepExecutionById(Long executionId) {
        return buildStepExecutionRepository.findById(executionId)
                .orElseThrow(() -> new BuildStepNotFoundException(executionId));
    }
}
