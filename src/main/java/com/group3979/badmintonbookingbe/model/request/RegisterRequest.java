package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String phone;
    private String email;
    private String name;
    private String password;
    private Gender gender;
    private Role role;
}
