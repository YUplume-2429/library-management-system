package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.ProfileUpdateRequest;
import com.library.dto.UserDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.RoleMapper;
import com.library.mapper.UserMapper;
import com.library.security.JwtTokenProvider;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String BORROWING = "BORROWING";
    private static final String ROLE_STUDENT = "ROLE_STUDENT";
    private static final String DEFAULT_STUDENT_PASSWORD = "123456";

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        User user = userMapper.selectByUsername(request.getUsername().trim());
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }
        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("User role does not exist");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUsername(), role.getRoleName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getUsername());

        return LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .role(role.getRoleName())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .build();
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User information cannot be empty");
        }
        if (StringUtils.hasText(user.getPassword()) && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        userMapper.insert(user);
        return user;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        validateUserDTO(userDTO);
        ensureUsernameNotExists(userDTO.getUsername());
        User user = buildUserFromDTO(userDTO);
        userMapper.insert(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public List<UserDTO> importUsers(List<UserDTO> userDTOList) {
        if (userDTOList == null || userDTOList.isEmpty()) {
            throw new IllegalArgumentException("Import data cannot be empty");
        }

        Set<String> importedUsernames = new HashSet<>();
        for (int index = 0; index < userDTOList.size(); index++) {
            UserDTO userDTO = userDTOList.get(index);
            validateUserDTO(userDTO);
            String username = userDTO.getUsername().trim();
            if (!importedUsernames.add(username)) {
                throw new IllegalArgumentException("Duplicate username in import data at row " + (index + 1) + ": " + username);
            }
            ensureUsernameNotExists(username);
            userDTO.setUsername(username);
        }

        return userDTOList.stream()
                .map(this::createUserWithoutDuplicateCheck)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return getAllUsers(null, null);
    }

    @Override
    public List<UserDTO> getAllUsers(String keyword, String roleName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim();
            queryWrapper.and(wrapper -> wrapper
                    .like("username", trimmedKeyword)
                    .or()
                    .like("name", trimmedKeyword)
            );
        }

        if (StringUtils.hasText(roleName)) {
            Role role = roleMapper.selectByRoleName(roleName.trim());
            if (role == null) {
                return List.of();
            }
            queryWrapper.eq("role_id", role.getRoleId());
        }

        queryWrapper.orderByDesc("created_at");
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getProfile(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateProfile(Integer userId, ProfileUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Profile information cannot be empty");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        user.setName(request.getName().trim());
        user.setEmail(StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : null);
        if (StringUtils.hasText(request.getNewPassword())) {
            if (!StringUtils.hasText(request.getCurrentPassword())) {
                throw new IllegalArgumentException("Current password is required to change password");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return convertToDTO(user);
    }

    @Override
    public UserDTO resetStudentPassword(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null || !ROLE_STUDENT.equals(role.getRoleName())) {
            throw new IllegalArgumentException("Only student passwords can be reset");
        }

        user.setPassword(passwordEncoder.encode(DEFAULT_STUDENT_PASSWORD));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return convertToDTO(user);
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return deleteUser(userId, false);
    }

    @Override
    @Transactional
    public boolean deleteUser(Integer userId, boolean force) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        List<BorrowRecord> activeRecords = borrowRecordMapper.selectList(
                new QueryWrapper<BorrowRecord>()
                        .eq("user_id", userId)
                        .eq("status", BORROWING)
        );

        if (!activeRecords.isEmpty() && !force) {
            throw new IllegalStateException("User has " + activeRecords.size() + " active borrow record(s)");
        }

        if (force) {
            returnBorrowedBooks(activeRecords);
        }

        borrowRecordMapper.delete(new QueryWrapper<BorrowRecord>().eq("user_id", userId));
        return userMapper.deleteById(userId) > 0;
    }

    private void returnBorrowedBooks(List<BorrowRecord> activeRecords) {
        LocalDateTime now = LocalDateTime.now();
        for (BorrowRecord record : activeRecords) {
            Book book = bookMapper.selectById(record.getBookId());
            if (book == null) {
                continue;
            }

            book.setStock((book.getStock() == null ? 0 : book.getStock()) + 1);
            book.setIsAvailable(true);
            book.setUpdatedAt(now);
            bookMapper.updateById(book);
        }
    }

    private void validateUserDTO(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("User information cannot be empty");
        }
        if (!StringUtils.hasText(userDTO.getUsername())
                || !StringUtils.hasText(userDTO.getPassword())
                || !StringUtils.hasText(userDTO.getName())
                || !StringUtils.hasText(userDTO.getRoleName())) {
            throw new IllegalArgumentException("Username, password, name and role are required");
        }
        if (!"ROLE_ADMIN".equals(userDTO.getRoleName()) && !"ROLE_STUDENT".equals(userDTO.getRoleName())) {
            throw new IllegalArgumentException("Role must be ROLE_ADMIN or ROLE_STUDENT");
        }
    }

    private void ensureUsernameNotExists(String username) {
        if (userMapper.selectByUsername(username.trim()) != null) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
    }

    private User buildUserFromDTO(UserDTO userDTO) {
        Role role = roleMapper.selectByRoleName(userDTO.getRoleName());
        if (role == null) {
            throw new IllegalArgumentException("Role does not exist: " + userDTO.getRoleName());
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(userDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName().trim());
        user.setEmail(StringUtils.hasText(userDTO.getEmail()) ? userDTO.getEmail().trim() : null);
        user.setIsActive(userDTO.getIsActive() == null || Boolean.TRUE.equals(userDTO.getIsActive()));
        user.setRoleId(role.getRoleId());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private UserDTO createUserWithoutDuplicateCheck(UserDTO userDTO) {
        User user = buildUserFromDTO(userDTO);
        userMapper.insert(user);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        Role role = roleMapper.selectById(user.getRoleId());
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .roleId(user.getRoleId())
                .roleName(role == null ? null : role.getRoleName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
