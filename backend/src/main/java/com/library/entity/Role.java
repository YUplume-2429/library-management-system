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
 * 角色实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
@Entity
@Table(name = "role")
public class Role {
    
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;
    
    /**
     * 角色名称（如ROLE_ADMIN、ROLE_STUDENT）
     */
    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;
    
    /**
     * 角色描述
     */
    @Column(name = "description")
    private String description;
    
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
