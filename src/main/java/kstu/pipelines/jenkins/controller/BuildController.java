package kstu.pipelines.jenkins.controller;

import kstu.pipelines.jenkins.dto.response.BuildResponse;
import kstu.pipelines.jenkins.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/{jobId}/builds")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;

    @PostMapping
    public ResponseEntity<BuildResponse> startBuild(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildService.startBuild(jobId));
    }

    @GetMapping
    public ResponseEntity<List<BuildResponse>> getAllBuilds(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok(buildService.getAllBuilds(jobId));
    }

    @GetMapping("/{buildId}")
    public ResponseEntity<BuildResponse> getBuild(@PathVariable("jobId") Long jobId,
                                                  @PathVariable("buildId") Long buildId) {
        return ResponseEntity.ok(buildService.getBuild(jobId, buildId));
    }
}
