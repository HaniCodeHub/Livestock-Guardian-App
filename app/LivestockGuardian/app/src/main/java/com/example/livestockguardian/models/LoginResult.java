package com.example.livestockguardian.models;

public class LoginResult {
    private final boolean success;
    private final String errorMessage;
    private final String userId;
    private final String userName;
    private final String email;
    private final String phone;
    private final String role;

    private LoginResult(
            boolean success,
            String errorMessage,
            String userId,
            String userName,
            String email,
            String phone,
            String role
    ) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static LoginResult success(
            String userId,
            String userName,
            String email,
            String phone,
            String role
    ) {
        return new LoginResult(true, null, userId, userName, email, phone, role);
    }

    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, errorMessage, null, null, null, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }
}
