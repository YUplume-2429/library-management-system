package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.ProfileUpdateRequest;
import com.library.dto.UserDTO;
import com.library.security.CustomUserDetails;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleName
    ) {
        try {
            List<UserDTO> users = userService.getAllUsers(keyword, roleName);
            return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
        } catch (Exception e) {
            log.error("Failed to fetch users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch users"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMyProfile() {
        try {
            UserDTO profile = userService.getProfile(getCurrentUser().getUserId());
            return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch profile"));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyProfile(@RequestBody ProfileUpdateRequest request) {
        try {
            UserDTO profile = userService.updateProfile(getCurrentUser().getUserId(), request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update profile"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User created successfully", createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create user"));
        }
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> importUsers(@RequestBody List<UserDTO> userDTOList) {
        try {
            List<UserDTO> importedUsers = userService.importUsers(userDTOList);
            return ResponseEntity.ok(ApiResponse.success("Users imported successfully", importedUsers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to import users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to import users"));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        try {
            boolean deleted = userService.deleteUser(userId, force);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "User not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete user"));
        }
    }

    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> resetStudentPassword(@PathVariable Integer userId) {
        try {
            UserDTO user = userService.resetStudentPassword(userId);
            return ResponseEntity.ok(ApiResponse.success("Student password reset to 123456", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to reset student password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reset student password"));
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalArgumentException("Not authenticated");
        }
        return userDetails;
    }
}
