package kstu.pipelines.jenkins.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.workspace")
public record WorkspaceProperties(Path root) {
}
