package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 修改密码请求（需要旧密码验证）。
 */
public class ChangePasswordRequest {

    @NotNull(message = "userId不能为空")
    private Integer userId;

    @NotBlank(message = "role不能为空")
    private String role;

    @NotBlank(message = "oldPassword不能为空")
    private String oldPassword;

    @NotBlank(message = "newPassword不能为空")
    private String newPassword;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
