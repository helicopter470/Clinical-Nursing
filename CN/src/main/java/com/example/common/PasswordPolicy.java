package com.example.common;

/**
 * 简单密码策略（可按需调整）。
 */
public class PasswordPolicy {
    private PasswordPolicy() {}

    public static void validateNewPassword(String newPassword, String oldPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("新密码长度至少8位");
        }
        if (oldPassword != null && newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        // 至少包含字母和数字（简易规则）
        boolean hasLetter = newPassword.matches(".*[A-Za-z].*");
        boolean hasDigit = newPassword.matches(".*\\d.*");
        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("新密码需同时包含字母和数字");
        }
    }
}
