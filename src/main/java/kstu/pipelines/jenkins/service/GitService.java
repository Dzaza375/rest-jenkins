package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.config.WorkspaceProperties;
import kstu.pipelines.jenkins.dto.execution.WorkspaceResult;
import kstu.pipelines.jenkins.exception.GitCommandException;
import kstu.pipelines.jenkins.exception.WorkspaceCreationException;
import kstu.pipelines.jenkins.exception.WorkspacePathAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {
    private final WorkspaceProperties workspaceProperties;

    public WorkspaceResult prepareWorkspace(
            Long jobId,
            Integer buildNumber,
            String repositoryUrl,
            String branch
    ) {
        Path workspacePath = workspaceProperties.root()
                .toAbsolutePath()
                .normalize()
                .resolve("job-" + jobId)
                .resolve("build-" + buildNumber)
                .normalize();

        Path logFile = workspacePath.getParent()
                .resolve("command-output-" + buildNumber + ".log");

        if (Files.exists(workspacePath)) {
            throw new WorkspacePathAlreadyExistsException(workspacePath);
        }

        try {
            Files.createDirectories(workspacePath);
        } catch (IOException e) {
            throw new WorkspaceCreationException(workspacePath, e);
        }

        boolean success = false;

        try {
            gitClone(workspacePath, logFile, branch, repositoryUrl);
            String commitHash = getCommitHash(workspacePath, logFile);

            success = true;
            return new WorkspaceResult(workspacePath, commitHash);
        } finally {
            if (!success) {
                deleteWorkspace(workspacePath);
            }
        }
    }

    private void gitClone(
            Path workspacePath,
            Path logFile,
            String branch,
            String repositoryUrl
    ) {
        executeGitCommand(
                null,
                logFile,
                "Git clone timed out",
                "git", "clone", "--branch", "branch",
                "--single-branch", repositoryUrl, workspacePath.toString()
        );
    }

    private String getCommitHash(Path workspacePath, Path logFile) {
        String commitHash = executeGitCommand(
                workspacePath,
                logFile,
                "Git rev-parse timed out",
                "git", "rev-parse", "HEAD"
        );

        if (commitHash.isBlank()) {
            throw new GitCommandException("Git returned an empty commit hash");
        }

        return commitHash;
    }

    private String executeGitCommand(
            Path workingDirectory,
            Path logFile,
            String timeoutMessage,
            String... command
    ) {
        Process process = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            if (workingDirectory != null) {
                processBuilder.directory(workingDirectory.toFile());
            }

            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(logFile.toFile());

            process = processBuilder.start();

            boolean completed = process.waitFor(5, TimeUnit.MINUTES);

            if (!completed) {
                terminateProcess(process);
                throw new GitCommandException(timeoutMessage);
            }

            String output = Files.readString(logFile, StandardCharsets.UTF_8);

            if (process.exitValue() != 0) {
                throw new GitCommandException(output);
            }

            return output.strip();
        } catch (IOException e) {
            throw new GitCommandException("Failed to execute Git command", e);
        } catch (InterruptedException e) {
            if (process.isAlive()) {
                terminateProcess(process);
            }

            Thread.currentThread().interrupt();
            throw new GitCommandException("Git command execution was interrupted", e);
        } finally {
            deleteLogFile(logFile);
        }
    }

    private void terminateProcess(Process process) {
        process.destroyForcibly();

        boolean interrupted = false;

        while (process.isAlive()) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void deleteLogFile(Path logFile) {
        try {
            Files.deleteIfExists(logFile);
        } catch (IOException e) {
            log.warn("Failed to delete Git log file: {}", logFile, e);
        }
    }

    private void deleteWorkspace(Path workspacePath) {
        try (Stream<Path> paths = Files.walk(workspacePath)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException | UncheckedIOException e) {
            log.warn("Failed to delete workspace: {}", workspacePath, e);
        }
    }
}
