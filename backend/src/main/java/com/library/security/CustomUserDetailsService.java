package com.library.security;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.RoleMapper;
import com.library.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom user details service.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        Role role = roleMapper.selectById(user.getRoleId());
        String roleName = role != null ? role.getRoleName() : "ROLE_STUDENT";
        boolean enabled = user.getIsActive() == null || Boolean.TRUE.equals(user.getIsActive());

        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                roleName,
                enabled
        );
    }
}
