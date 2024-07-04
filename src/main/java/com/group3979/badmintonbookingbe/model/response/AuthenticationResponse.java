package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private Long accountId;
    private String phone;
    private String email;
    private String name;
    private Role role;
    private Gender gender;
    private Long supervisorID;
    private AccountStatus accountStatus;
    private String avatar;
    private LocalDate signupDate;
}
