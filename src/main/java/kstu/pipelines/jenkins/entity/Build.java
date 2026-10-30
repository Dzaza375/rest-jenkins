package kstu.pipelines.jenkins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kstu.pipelines.jenkins.enums.BuildStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "builds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_build_job_number",
                        columnNames = {"job_id", "build_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Job must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Positive(message = "Build number must be positive")
    @Column(name = "build_number", nullable = false)
    private Integer buildNumber;

    @NotNull(message = "Build status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BuildStatus status = BuildStatus.QUEUED;

    @Size(max = 64, message = "Commit hash must not exceed 64 characters")
    @Column(length = 64)
    private String commitHash;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Column(columnDefinition = "TEXT")
    private String consoleOutput = "";

    @Builder
    public Build(Job job, Integer buildNumber, BuildStatus status, String commitHash) {
        this.job = job;
        this.buildNumber = buildNumber;
        this.status = status;
        this.commitHash = commitHash;
    }
}
