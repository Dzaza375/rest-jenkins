package kstu.pipelines.jenkins.controller;

import kstu.pipelines.jenkins.dto.response.BuildConsoleResponse;
import kstu.pipelines.jenkins.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/builds/{buildId}/console")
@RequiredArgsConstructor
public class BuildConsoleController {
    private final BuildService buildService;

    @GetMapping
    public ResponseEntity<BuildConsoleResponse> getBuildConsole(@PathVariable("buildId") Long buildId) {
        return ResponseEntity.ok(buildService.getBuildConsole(buildId));
    }
}
