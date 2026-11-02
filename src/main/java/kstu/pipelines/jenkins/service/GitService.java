package kstu.pipelines.jenkins.service;

import kstu.pipelines.jenkins.config.WorkspaceProperties;
import kstu.pipelines.jenkins.dto.execution.WorkspaceResult;
import kstu.pipelines.jenkins.exception.GitCommandException;
import kstu.pipelines.jenkins.exception.WorkspaceCreationException;
import kstu.pipelines.jenkins.exception.WorkspacePathAlreadyExistsException;
import kstu.pipelines.jenkins.service.process.ProcessUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
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

    public String getRemoteCommitHash(String repositoryUrl, String branch) {
        Path logFile = workspaceProperties.root()
                .toAbsolutePath()
                .normalize()
                .resolve("remote-" + UUID.randomUUID() + ".log");

        try {
            Files.createDirectories(logFile.getParent());
        } catch (IOException e) {
            throw new WorkspaceCreationException(logFile.getParent(), e);
        }

        return getRemoteCommitHash(repositoryUrl, branch, logFile);
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
                "git", "clone", "--branch", branch,
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

    private String getRemoteCommitHash(String repositoryUrl, String branch, Path logFile) {
        String output = executeGitCommand(
                null,
                logFile,
                "Git ls-remote timed out",
                "git", "ls-remote", repositoryUrl, "refs/heads/" + branch
        );

        String[] parts = output.split("\\s+");

        if (parts.length == 0 || parts[0].isBlank()) {
            throw new GitCommandException("Git returned an empty remote commit hash");
        }

        return parts[0];
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
                ProcessUtils.terminate(process);
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
            if (process != null && process.isAlive()) {
                ProcessUtils.terminate(process);
            }

            Thread.currentThread().interrupt();
            throw new GitCommandException("Git command execution was interrupted", e);
        } finally {
            deleteLogFile(logFile);
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
