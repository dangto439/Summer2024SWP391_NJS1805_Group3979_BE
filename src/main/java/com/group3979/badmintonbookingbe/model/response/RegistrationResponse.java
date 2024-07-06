package com.group3979.badmintonbookingbe.model.response;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private long registrationId;
    private long contestId;
    private long accountId;
    private String registrationDate;
}
