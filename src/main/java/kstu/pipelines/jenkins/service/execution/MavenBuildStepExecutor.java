package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.exception.CommandExecutionException;
import kstu.pipelines.jenkins.service.process.CommandExecutor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class MavenBuildStepExecutor implements BuildStepExecutor {
    @Override
    public String execute(BuildStepExecutionContext context, Path workspace) {
        if (context.command() == null || context.command().isBlank()) {
            throw new CommandExecutionException("Maven command must not be empty");
        }

        List<String> command = new ArrayList<>();
        command.add("mvn.cmd");
        command.addAll(Arrays.asList(context.command().trim().split("\\s+")));

        return CommandExecutor.execute(command, workspace, "Maven");
    }
}