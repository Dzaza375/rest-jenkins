package kstu.pipelines.jenkins.service.factory;

import kstu.pipelines.jenkins.enums.StepType;
import kstu.pipelines.jenkins.service.execution.BuildStepExecutor;
import kstu.pipelines.jenkins.service.execution.MavenBuildStepExecutor;
import org.springframework.stereotype.Component;

@Component
public class MavenBuildStepExecutorCreator extends BuildStepExecutorCreator {
    @Override
    protected BuildStepExecutor createExecutor() {
        return new MavenBuildStepExecutor();
    }

    @Override
    public StepType getSupportedType() {
        return StepType.MAVEN;
    }
}
