package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * Token过期时间（毫秒）
     */
    private Long expiresIn;
}
