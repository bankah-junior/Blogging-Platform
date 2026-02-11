package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.entity.Role;
import com.amalitech.SpringBootBloggingApp.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerService.class);

    private final RoleRepository roleRepository;

    public DataInitializerService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        createRoleIfNotExists("ADMIN", "Administrator with full system access");
        createRoleIfNotExists("AUTHOR", "Content creator who can write and manage posts");
        createRoleIfNotExists("READER", "Basic user who can read and comment on posts");
        
        logger.info("Role initialization completed");
    }

    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role(roleName, description);
            roleRepository.save(role);
            logger.info("Created role: {}", roleName);
        }
    }
}
