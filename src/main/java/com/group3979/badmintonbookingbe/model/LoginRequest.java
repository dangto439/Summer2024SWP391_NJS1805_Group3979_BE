package com.group3979.badmintonbookingbe.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}
