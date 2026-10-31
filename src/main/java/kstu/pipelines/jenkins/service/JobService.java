package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.dto.request.CreateJobRequest;
import kstu.pipelines.jenkins.dto.request.UpdateJobRequest;
import kstu.pipelines.jenkins.dto.response.JobResponse;
import kstu.pipelines.jenkins.entity.Job;
import kstu.pipelines.jenkins.exception.DuplicateJobNameException;
import kstu.pipelines.jenkins.exception.JobNotFoundException;
import kstu.pipelines.jenkins.mapper.JobMapper;
import kstu.pipelines.jenkins.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Transactional
    public JobResponse createJob(CreateJobRequest request) {
        if (jobRepository.existsByName(request.name())) {
            throw new DuplicateJobNameException(request.name());
        }

        Job job = Job.builder()
                .name(request.name())
                .repositoryUrl(request.repositoryUrl())
                .branch(request.branch())
                .pollScmIntervalMinutes(request.pollScmIntervalMinutes())
                .periodicBuildIntervalMinutes(request.periodicBuildIntervalMinutes())
                .build();

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        return jobMapper.toResponse(findJobById(id));
    }

    @Transactional
    public JobResponse updateJob(Long id, UpdateJobRequest request) {
        Job job = findJobById(id);

        if (!job.getName().equals(request.name()) && jobRepository.existsByName(request.name())) {
            throw new DuplicateJobNameException(request.name());
        }

        job.setName(request.name());
        job.setRepositoryUrl(request.repositoryUrl());
        job.setBranch(request.branch());
        job.setPollScmIntervalMinutes(request.pollScmIntervalMinutes());
        job.setPeriodicBuildIntervalMinutes(request.periodicBuildIntervalMinutes());

        return jobMapper.toResponse(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        jobRepository.delete(findJobById(id));
    }

    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }
}
