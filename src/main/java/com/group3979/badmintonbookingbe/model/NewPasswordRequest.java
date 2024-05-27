package com.group3979.badmintonbookingbe.model;


import lombok.Data;

@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
}
