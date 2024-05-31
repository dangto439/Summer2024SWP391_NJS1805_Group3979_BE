package com.group3979.badmintonbookingbe.model;

import com.group3979.badmintonbookingbe.eNum.Gender;
import lombok.Data;

@Data
public class ProfileRequest {
    private String name;
    private String phone;
    private String email;
    private Gender gender;
    private String avatar;
}
