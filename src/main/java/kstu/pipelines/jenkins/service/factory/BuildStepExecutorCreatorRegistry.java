package kstu.pipelines.jenkins.service.factory;

import kstu.pipelines.jenkins.enums.StepType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BuildStepExecutorCreatorRegistry {
    private final Map<StepType, BuildStepExecutorCreator> creators;

    public BuildStepExecutorCreatorRegistry(List<BuildStepExecutorCreator> creators) {
        this.creators = creators.stream()
                .collect(Collectors.toMap(
                        BuildStepExecutorCreator::getSupportedType,
                        Function.identity()
                ));
    }

    public BuildStepExecutorCreator getCreator(StepType type) {
        Objects.requireNonNull(type, "Step type must not be null");

        BuildStepExecutorCreator creator = creators.get(type);

        if (creator == null) {
            throw new IllegalArgumentException("Unsupported build step type: " + type);
        }

        return creator;
    }
}
