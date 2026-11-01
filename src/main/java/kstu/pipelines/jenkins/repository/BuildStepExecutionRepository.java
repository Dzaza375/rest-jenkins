package kstu.pipelines.jenkins.repository;

import kstu.pipelines.jenkins.entity.BuildStepExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildStepExecutionRepository extends JpaRepository<BuildStepExecution, Long> {
}
