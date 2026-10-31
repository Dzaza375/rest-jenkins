package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.Build;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildRepository extends JpaRepository<Build, Long> {
    Optional<Build> findByIdAndJobId(Long buildId, Long jobId);
    List<Build> findByJobIdOrderByBuildNumberDesc(Long jobId);
    Optional<Build> findTopByJobIdOrderByBuildNumberDesc(Long jobId);
    boolean existsByJobIdAndBuildNumber(Long jobId, Integer buildNumber);
}
