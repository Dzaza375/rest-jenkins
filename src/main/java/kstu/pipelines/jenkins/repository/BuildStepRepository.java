package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.BuildStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildStepRepository extends JpaRepository<BuildStep, Long> {
    List<BuildStep> findByJobIdOrderByPositionAsc(Long jobId);
    boolean existsByJobIdAndPosition(Long jobId, Integer position);
}
