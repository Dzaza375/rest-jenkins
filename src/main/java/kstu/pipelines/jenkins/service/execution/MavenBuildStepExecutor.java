package kstu.pipelines.jenkins.service.execution;

import kstu.pipelines.jenkins.dto.execution.BuildStepExecutionContext;
import kstu.pipelines.jenkins.exception.CommandExecutionException;
import kstu.pipelines.jenkins.service.process.ProcessUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> outputFuture = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            if (workspace != null) {
                processBuilder.directory(workspace.toFile());
            }

            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            Process finalProcess= process;

            outputFuture = executor.submit(() -> readOutput(finalProcess));

            boolean completed = process.waitFor(5, TimeUnit.MINUTES);

            if (!completed) {
                ProcessUtils.terminate(process);

                String output = getOutput(outputFuture);

                throw new CommandExecutionException("Maven command execution timed out\n" + output);
            }

            String output = getOutput(outputFuture);

            if (process.exitValue() != 0) {
                throw new CommandExecutionException("Maven command failed with exit code: " + process.exitValue()
                + "\n" + output);
            }

            return output;
        } catch (IOException e) {
            throw new CommandExecutionException("Failed to execute Maven command", e);
        } catch (InterruptedException e) {
            if (process != null && process.isAlive()) {
                ProcessUtils.terminate(process);
            }

            Thread.currentThread().interrupt();
            throw new CommandExecutionException("Maven command execution was interrupted", e);
        } finally {
            executor.shutdownNow();
        }
    }

    private String getOutput(Future<String> outputFuture) {
        try {
            return outputFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommandExecutionException(
                    "Interrupted while reading Maven output", e
            );
        } catch (ExecutionException e) {
            throw new CommandExecutionException(
                    "Failed to read Maven output", e.getCause()
            );
        } catch (TimeoutException e) {
            outputFuture.cancel(true);
            throw new CommandExecutionException(
                    "Timed out while reading Maven output", e
            );
        }
    }

    private String readOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        process.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        return output.toString();
    }
}