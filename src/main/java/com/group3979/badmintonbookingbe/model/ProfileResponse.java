package com.group3979.badmintonbookingbe.model;

import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private String name;
    private String phone;
    private String password;
    private String email;
    private Gender gender;
    private Role role;
    private String avatar;
}
