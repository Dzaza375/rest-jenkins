package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.exception.CommandExecutionException;
import kstu.pipelines.jenkins.service.process.CommandExecutor;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;

public class ShellBuildStepExecutor implements BuildStepExecutor {
    @Override
    public String execute(BuildStepExecutionContext context, Path workspace) {
        if (context.command() == null || context.command().isBlank()) {
            throw new CommandExecutionException("Shell command must not be empty");
        }

        List<String> command = List.of("cmd.exe", "/c", context.command());

        return CommandExecutor.execute(command, workspace, "Shell");
    }
}
