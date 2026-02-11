package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> getAllRoles();

    Optional<Role> getRoleByName(String name);

    void assignRoleToUser(String userId, String roleName);

    void removeRoleFromUser(String userId, String roleName);

    List<Role> getUserRoles(String userId);

    Role createRole(String name, String description);
}
