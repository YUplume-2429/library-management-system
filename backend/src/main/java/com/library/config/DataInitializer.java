package com.library.config;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.RoleMapper;
import com.library.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Ensures the demo data exists so the sample accounts work out of the box.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = ensureRole("ROLE_ADMIN", "Administrator with full permissions");
        Role studentRole = ensureRole("ROLE_STUDENT", "Student with read-only permissions");

        ensureUser("admin", "admin123", "Admin", "admin@library.com", adminRole.getRoleId());
        ensureUser("student1", "student123", "Student One", "student1@library.com", studentRole.getRoleId());
        ensureUser("student2", "student123", "Student Two", "student2@library.com", studentRole.getRoleId());
    }

    private Role ensureRole(String roleName, String description) {
        Role role = roleMapper.selectByRoleName(roleName);
        if (role != null) {
            return role;
        }

        Role newRole = new Role();
        newRole.setRoleName(roleName);
        newRole.setDescription(description);
        roleMapper.insert(newRole);

        Role createdRole = roleMapper.selectByRoleName(roleName);
        if (createdRole == null) {
            throw new IllegalStateException("Failed to create required role: " + roleName);
        }

        log.info("Created missing role: {}", roleName);
        return createdRole;
    }

    private void ensureUser(String username, String rawPassword, String name, String email, Integer roleId) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setIsActive(true);
            newUser.setRoleId(roleId);
            userMapper.insert(newUser);
            log.info("Created missing user: {}", username);
            return;
        }

        boolean changed = false;
        if (!StringUtils.hasText(user.getPassword()) || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            changed = true;
        }
        if (!Objects.equals(user.getRoleId(), roleId)) {
            user.setRoleId(roleId);
            changed = true;
        }
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            user.setIsActive(true);
            changed = true;
        }

        if (changed) {
            userMapper.updateById(user);
            log.info("Synchronized default user: {}", username);
        }
    }
}
