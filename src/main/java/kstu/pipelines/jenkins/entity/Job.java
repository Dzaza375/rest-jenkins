package kstu.pipelines.jenkins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job name must not be blank")
    @Size(max = 100, message = "Job name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "Repository URL must not be blank")
    @Size(max = 500, message = "Repository URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String repositoryUrl;

    @NotBlank(message = "Branch must not be blank")
    @Size(max = 255, message = "Branch must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String branch;

    @Column(nullable = false)
    private boolean enabled = true;

    @Size(max = 64, message = "Commit hash must not exceed 64 characters")
    @Column(length = 64)
    private String lastCommitHash;

    private LocalDateTime lastScmPollAt;
    private LocalDateTime lastPeriodicBuildAt;

    @Positive(message = "SCM polling interval must be positive")
    private Integer pollScmIntervalMinutes;

    @Positive(message = "Periodic build interval must be positive")
    private Integer periodicBuildIntervalMinutes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("buildNumber DESC")
    private List<Build> builds = new ArrayList<>();

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<BuildStep> buildSteps = new ArrayList<>();

    @Builder
    public Job(String name, String repositoryUrl, String branch, Integer pollScmIntervalMinutes, Integer periodicBuildIntervalMinutes) {
        this.name = name;
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        this.pollScmIntervalMinutes = pollScmIntervalMinutes;
        this.periodicBuildIntervalMinutes = periodicBuildIntervalMinutes;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
