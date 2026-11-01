package kstu.pipelines.jenkins.service.factory;

import kstu.pipelines.jenkins.enums.StepType;
import kstu.pipelines.jenkins.service.execution.BuildStepExecutor;
import kstu.pipelines.jenkins.service.execution.ShellBuildStepExecutor;
import org.springframework.stereotype.Component;

@Component
public class ShellBuildStepExecutorCreator extends BuildStepExecutorCreator {
    @Override
    protected BuildStepExecutor createExecutor() {
        return new ShellBuildStepExecutor();
    }

    @Override
    public StepType getSupportedType() {
        return StepType.SHELL;
    }
}
