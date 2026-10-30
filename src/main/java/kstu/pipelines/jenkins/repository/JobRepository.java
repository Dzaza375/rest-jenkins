package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByName(String name);
    boolean existsByName(String name);
}
