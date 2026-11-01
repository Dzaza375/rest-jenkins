package kstu.pipelines.jenkins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kstu.pipelines.jenkins.enums.StepStatus;
import kstu.pipelines.jenkins.enums.StepType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "build_step_execution",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_build_step_execution_position",
                        columnNames = {"build_id", "position"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class BuildStepExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Build must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    Build build;

    @NotBlank(message = "Step name must not be blank")
    @Size(max = 100, message = "Step name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    String name;

    @NotNull(message = "Step type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    StepType type;

    @NotBlank(message = "Command must not be blank")
    @Size(max = 2000, message = "Command must not exceed 2000 characters")
    @Column(nullable = false, length = 2000)
    String command;

    @NotNull(message = "Step position must not be null")
    @Positive(message = "Step position must be positive")
    @Column(nullable = false)
    Integer position;

    @NotNull(message = "Step status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    StepStatus status = StepStatus.QUEUED;

    @Column(columnDefinition = "TEXT")
    String consoleOutput;

    @Builder
    public BuildStepExecution(String name, StepType type, String command, Integer position) {
        this.name = name;
        this.type = type;
        this.command = command;
        this.position = position;
    }
}
