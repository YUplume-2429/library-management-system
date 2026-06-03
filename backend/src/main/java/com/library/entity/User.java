package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
@Entity
@Table(name = "user")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    /**
     * 用户名（登录账号）
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    /**
     * 密码（加密存储）
     */
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * 用户姓名
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;
    
    /**
     * 是否激活
     */
    @Column(name = "is_active")
    private Boolean isActive;
    
    /**
     * 角色ID（外键关联到role表）
     */
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
