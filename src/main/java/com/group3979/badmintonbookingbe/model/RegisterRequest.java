package com.group3979.badmintonbookingbe.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String email;
    private String name;
    private String password;
}
