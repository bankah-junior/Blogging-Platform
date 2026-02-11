package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.Role;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.RoleRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    public void assignRoleToUser(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

        Set<Role> userRoles = user.getRoles();
        if (userRoles.contains(role)) {
            throw new RuntimeException("User already has role: " + roleName);
        }

        userRoles.add(role);
        user.setRoles(userRoles);
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

        Set<Role> userRoles = user.getRoles();
        if (!userRoles.contains(role)) {
            throw new RuntimeException("User does not have role: " + roleName);
        }

        userRoles.remove(role);
        user.setRoles(userRoles);
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Override
    public List<Role> getUserRoles(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return new ArrayList<>(user.getRoles());
    }

    @Override
    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Role already exists with name: " + name);
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(System.currentTimeMillis());

        return roleRepository.save(role);
    }
}
