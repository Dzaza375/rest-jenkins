package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.dto.request.CreateBuildStepRequest;
import kstu.pipelines.jenkins.dto.request.UpdateBuildStepRequest;
import kstu.pipelines.jenkins.dto.response.BuildStepResponse;
import kstu.pipelines.jenkins.entity.BuildStep;
import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.exception.BuildStepNotFoundException;
import kstu.pipelines.jenkins.exception.DuplicateBuildStepPositionException;
import kstu.pipelines.jenkins.exception.JobNotFoundException;
import kstu.pipelines.jenkins.mapper.BuildStepMapper;
import kstu.pipelines.jenkins.repository.BuildStepRepository;
import kstu.pipelines.jenkins.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildStepService {
    private final JobRepository jobRepository;
    private final BuildStepRepository buildStepRepository;
    private final BuildStepMapper buildStepMapper;

    @Transactional
    public BuildStepResponse createBuildStep(Long jobId, CreateBuildStepRequest request) {
        Job job = findJobById(jobId);

        checkBuildStepDuplicatePosition(jobId, request.position());

        BuildStep buildStep = BuildStep.builder()
                .job(job)
                .name(request.name())
                .type(request.type())
                .command(request.command())
                .position(request.position())
                .build();

        return buildStepMapper.toResponse(buildStepRepository.save(buildStep));
    }

    @Transactional(readOnly = true)
    public List<BuildStepResponse> getAllBuildSteps(Long jobId) {
        findJobById(jobId);

        return buildStepRepository.findByJobIdOrderByPositionAsc(jobId)
                .stream()
                .map(buildStepMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BuildStepResponse getBuildStep(Long jobId, Long buildStepId) {
        return buildStepMapper.toResponse(findBuildStepByIdAndJobId(buildStepId, jobId));
    }

    @Transactional
    public BuildStepResponse updateBuildStep(Long jobId, Long buildStepId, UpdateBuildStepRequest request) {
        BuildStep buildStep = findBuildStepByIdAndJobId(buildStepId, jobId);

        checkBuildStepDuplicatePosition(jobId, request.position(), buildStepId);

        buildStep.setName(request.name());
        buildStep.setType(request.type());
        buildStep.setCommand(request.command());
        buildStep.setPosition(request.position());

        return buildStepMapper.toResponse(buildStep);
    }

    @Transactional
    public void deleteBuildStep(Long jobId, Long buildStepId) {
        BuildStep buildStep = findBuildStepByIdAndJobId(buildStepId, jobId);
        buildStepRepository.delete(buildStep);
    }

    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    private BuildStep findBuildStepByIdAndJobId(Long buildStepId, Long jobId) {
        return buildStepRepository.findByIdAndJobId(buildStepId, jobId)
                .orElseThrow(() -> new BuildStepNotFoundException(
                        "BuildStep with id " + buildStepId
                        + " not found in Job with id " + jobId
                ));
    }

    private void checkBuildStepDuplicatePosition(Long jobId, Integer position) {
        if (buildStepRepository.existsByJobIdAndPosition(jobId, position)) {
            throw new DuplicateBuildStepPositionException(jobId, position);
        }
    }

    private void checkBuildStepDuplicatePosition(Long jobId, Integer position, Long buildStepId) {
        if (buildStepRepository.existsByJobIdAndPositionAndIdNot(jobId, position, buildStepId)) {
            throw new DuplicateBuildStepPositionException(jobId, position);
        }
    }
}
