package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Contest;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private long registrationId;
    private Contest contest;
    private Account account;
    private String registrationDate;
}
