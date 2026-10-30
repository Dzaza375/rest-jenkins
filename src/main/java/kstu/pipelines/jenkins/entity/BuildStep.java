package kstu.pipelines.jenkins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kstu.pipelines.jenkins.enums.StepType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "build_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_step_job_position",
                        columnNames = {"job_id", "position"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class BuildStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Job must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @NotBlank(message = "Step name must not be blank")
    @Size(max = 100, message = "Step name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Step type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepType type;

    @NotBlank(message = "Command must not be blank")
    @Size(max = 2000, message = "Command must not exceed 2000 characters")
    @Column(nullable = false, length = 2000)
    private String command;

    @Positive(message = "Step position must be positive")
    @Column(nullable = false)
    private Integer position;

    @Builder
    public BuildStep(Job job, String name, StepType type, String command, Integer position) {
        this.job = job;
        this.name = name;
        this.type = type;
        this.command = command;
        this.position = position;
    }
}
