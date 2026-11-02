package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByName(String name);
    boolean existsByName(String name);
    List<Job> findAllByEnabledTrueAndPollScmIntervalMinutesIsNotNull();
    List<Job> findAllByEnabledTrueAndPeriodicBuildIntervalMinutesIsNotNull();
}
