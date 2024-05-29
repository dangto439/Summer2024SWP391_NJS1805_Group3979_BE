package com.group3979.badmintonbookingbe.model;

import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String email;
    private String name;
    private String password;
    private Gender gender;
    private Role role;
    private AccountStatus accountStatus;
}
