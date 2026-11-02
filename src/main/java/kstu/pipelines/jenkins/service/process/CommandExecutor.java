package kstu.pipelines.jenkins.service.process;

import kstu.pipelines.jenkins.exception.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;

public final class CommandExecutor {
    private CommandExecutor() {
    }

    public static String execute(List<String> command, Path workspace, String commandName) {
        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            if (workspace != null) {
                processBuilder.directory(workspace.toFile());
            }

            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            Process finalProcess = process;
            Future<String> outputFuture = executor.submit(
                    () -> readOutput(finalProcess)
            );

            boolean completed = process.waitFor(5, TimeUnit.MINUTES);

            if (!completed) {
                ProcessUtils.terminate(process);

                String output = getOutput(outputFuture);

                throw new CommandExecutionException(
                        commandName + " command execution timed out\n" + output
                );
            }

            String output = getOutput(outputFuture);

            if (process.exitValue() != 0) {
                throw new CommandExecutionException(
                        commandName + " command failed with exit code: "
                                + process.exitValue() + "\n" + output
                );
            }

            return output;

        } catch (IOException e) {
            throw new CommandExecutionException(
                    "Failed to execute " + commandName + " command", e
            );
        } catch (InterruptedException e) {
            if (process != null && process.isAlive()) {
                ProcessUtils.terminate(process);
            }

            Thread.currentThread().interrupt();

            throw new CommandExecutionException(
                    commandName + " command execution was interrupted", e
            );
        } finally {
            executor.shutdownNow();
        }
    }

    private static String getOutput(Future<String> outputFuture) {
        try {
            return outputFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new CommandExecutionException(
                    "Interrupted while reading command output", e
            );
        } catch (ExecutionException e) {
            throw new CommandExecutionException(
                    "Failed to read command output", e.getCause()
            );
        } catch (TimeoutException e) {
            outputFuture.cancel(true);

            throw new CommandExecutionException("Timed out while reading command output", e);
        }
    }

    private static String readOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader( process.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        return output.toString();
    }
}
