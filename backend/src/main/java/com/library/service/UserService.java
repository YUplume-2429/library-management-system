package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.ProfileUpdateRequest;
import com.library.dto.UserDTO;
import com.library.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户ID获取用户信息
     */
    User getUserById(Integer userId);
    
    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 创建新用户
     */
    User createUser(User user);

    /**
     * 管理员创建新用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 管理员批量导入用户
     */
    List<UserDTO> importUsers(List<UserDTO> userDTOList);

    /**
     * 获取所有用户
     */
    List<UserDTO> getAllUsers();

    /**
     * 按关键词和角色筛选用户
     */
    List<UserDTO> getAllUsers(String keyword, String roleName);

    /**
     * 获取当前用户个人资料
     */
    UserDTO getProfile(Integer userId);

    /**
     * 更新当前用户允许修改的个人资料
     */
    UserDTO updateProfile(Integer userId, ProfileUpdateRequest request);

    /**
     * 管理员重置学生密码为默认密码
     */
    UserDTO resetStudentPassword(Integer userId);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Integer userId);

    /**
     * 删除用户，可选择是否强制归还在借图书并清理借阅记录
     */
    boolean deleteUser(Integer userId, boolean force);
}
