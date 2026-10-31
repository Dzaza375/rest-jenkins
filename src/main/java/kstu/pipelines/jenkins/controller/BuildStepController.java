package kstu.pipelines.jenkins.controller;

import jakarta.validation.Valid;
import kstu.pipelines.jenkins.dto.request.CreateBuildStepRequest;
import kstu.pipelines.jenkins.dto.request.UpdateBuildStepRequest;
import kstu.pipelines.jenkins.dto.response.BuildStepResponse;
import kstu.pipelines.jenkins.service.BuildStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/{jobId}/steps")
@RequiredArgsConstructor
public class BuildStepController {
    private final BuildStepService buildStepService;

    @PostMapping
    public ResponseEntity<BuildStepResponse> createBuildStep(@PathVariable("jobId") Long jobId,
                                                             @Valid @RequestBody CreateBuildStepRequest request) {
        BuildStepResponse response = buildStepService.createBuildStep(jobId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BuildStepResponse>> getAllBuildSteps(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok(buildStepService.getAllBuildSteps(jobId));
    }

    @GetMapping("/{stepId}")
    public ResponseEntity<BuildStepResponse> getBuildStepById(@PathVariable("jobId") Long jobId,
                                                              @PathVariable("stepId") Long stepId) {
        return ResponseEntity.ok(buildStepService.getBuildStep(jobId, stepId));
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<BuildStepResponse> updateBuildStep(@PathVariable("jobId") Long jobId,
                                                             @PathVariable("stepId") Long stepId,
                                                             @Valid @RequestBody UpdateBuildStepRequest request) {
        return ResponseEntity.ok(buildStepService.updateBuildStep(jobId, stepId, request));
    }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteBuildStep(@PathVariable("jobId") Long jobId,
                                                @PathVariable("stepId") Long stepId) {
        buildStepService.deleteBuildStep(jobId, stepId);
        return ResponseEntity.noContent().build();
    }
}
