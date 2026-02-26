package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.service.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Exposes in-memory runtime performance metrics collected by
 * {@link com.amalitech.SpringBootBloggingApp.aspects.PerformanceAspect}.
 *
 * All endpoints require {@code ROLE_ADMIN} — metrics data can expose
 * internal method names and timing information.
 */
@RestController
@RequestMapping("/api/v1/metrics")
@Tag(name = "Metrics", description = "Runtime performance metrics for all service methods")
@PreAuthorize("hasRole('ADMIN')")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Full snapshot: every tracked method with invocation count, avg/max latency,
     * total latency, and error count.
     */
    @GetMapping
    @Operation(summary = "Get full metrics snapshot for all tracked service methods")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Object>>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Metrics snapshot retrieved", metricsService.getSnapshot()));
    }

    /**
     * Metrics for a single method.
     * {@code methodName} should be the fully-qualified form returned by the snapshot,
     * e.g. {@code com.amalitech.SpringBootBloggingApp.service.impl.PostServiceImpl.getAll}.
     */
    @GetMapping("/{methodName}")
    @Operation(summary = "Get metrics for a specific service method")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMethod(
            @PathVariable String methodName) {
        Map<String, Object> data = metricsService.getMethodSnapshot(methodName);
        if (data == null) {
            return ResponseEntity.ok(
                    ApiResponse.error("No metrics found for method: " + methodName));
        }
        return ResponseEntity.ok(ApiResponse.success("Method metrics retrieved", data));
    }

    /**
     * Summary: total number of methods currently being tracked.
     */
    @GetMapping("/summary")
    @Operation(summary = "Get a summary of tracked metrics (method count)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        Map<String, Object> summary = Map.of(
                "trackedMethods", metricsService.getTrackedMethodCount(),
                "snapshot",       metricsService.getSnapshot()
        );
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved", summary));
    }

    /**
     * Resets all collected metrics.
     * Useful before running a performance benchmark to get a clean baseline.
     */
    @DeleteMapping("/reset")
    @Operation(summary = "Reset all collected metrics")
    public ResponseEntity<ApiResponse<Void>> reset() {
        metricsService.reset();
        return ResponseEntity.ok(ApiResponse.success("Metrics reset", null));
    }
}
