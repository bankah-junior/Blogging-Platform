package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.service.SecurityAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@Tag(name = "Security Audit", description = "Security monitoring and audit endpoints")
public class SecurityAuditController {

    private final SecurityAuditService securityAuditService;

    public SecurityAuditController(SecurityAuditService securityAuditService) {
        this.securityAuditService = securityAuditService;
    }

    @GetMapping("/test")
    @Operation(summary = "Test authentication", description = "Test if authentication is working")
    public ResponseEntity<Map<String, Object>> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", auth != null && auth.isAuthenticated());
        response.put("username", auth != null ? auth.getName() : null);
        response.put("authorities", auth != null ? auth.getAuthorities() : null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audit/report")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get security audit report", description = "Returns comprehensive security statistics and metrics (Admin only)")
    public ResponseEntity<Map<String, Object>> getSecurityReport() {
        return ResponseEntity.ok(securityAuditService.getSecurityReport());
    }

    @GetMapping("/audit/events")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get recent security events", description = "Returns recent security events (Admin only)")
    public ResponseEntity<List<SecurityAuditService.SecurityEvent>> getRecentSecurityEvents(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(securityAuditService.getRecentSecurityEvents(limit));
    }

    @GetMapping("/audit/login-attempts/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get login attempts by email", description = "Returns login attempt history for a specific email (Admin only)")
    public ResponseEntity<Map<String, List<SecurityAuditService.LoginAttempt>>> getLoginAttemptsByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(securityAuditService.getLoginAttemptsByEmail(email));
    }

    @GetMapping("/audit/locked-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check if account is locked", description = "Checks if a specific email is currently locked (Admin only)")
    public ResponseEntity<Map<String, Object>> checkAccountLocked(@RequestParam String email) {
        boolean isLocked = securityAuditService.isAccountLocked(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("isLocked", isLocked);
        return ResponseEntity.ok(response);
    }
}
