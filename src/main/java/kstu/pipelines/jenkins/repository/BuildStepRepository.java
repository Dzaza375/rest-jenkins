package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.BuildStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildStepRepository extends JpaRepository<BuildStep, Long> {
    Optional<BuildStep> findByIdAndJobId(Long id, Long jobId);
    List<BuildStep> findByJobIdOrderByPositionAsc(Long jobId);
    boolean existsByJobIdAndPosition(Long jobId, Integer position);
    boolean existsByJobIdAndPositionAndIdNot(Long jobId, Integer position, Long buildStepId);
}
