package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.entity.Role;
import com.amalitech.SpringBootBloggingApp.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin-only operations for system management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final RoleService roleService;

    public AdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all roles", description = "Get all available roles in the system")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assign a role to a specific user")
    public ResponseEntity<Map<String, String>> assignRole(
            @PathVariable String userId,
            @PathVariable String roleName) {
        roleService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok(Map.of(
                "message", "Role " + roleName + " assigned to user successfully"
        ));
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Remove a role from a specific user")
    public ResponseEntity<Map<String, String>> removeRole(
            @PathVariable String userId,
            @PathVariable String roleName) {
        roleService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok(Map.of(
                "message", "Role " + roleName + " removed from user successfully"
        ));
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user roles", description = "Get all roles assigned to a specific user")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(roleService.getUserRoles(userId));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new role", description = "Create a new role in the system")
    public ResponseEntity<Role> createRole(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        Role role = roleService.createRole(name, description);
        return ResponseEntity.ok(role);
    }
}
