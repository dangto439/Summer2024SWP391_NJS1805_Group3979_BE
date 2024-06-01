package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.Gender;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileRequest {
    private String name;
    private String phone;
    private Gender gender;
    private String avatar;
}
