package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人资料更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String name;

    private String email;

    private String currentPassword;

    private String newPassword;
}
