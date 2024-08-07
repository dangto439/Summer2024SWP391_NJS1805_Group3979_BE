package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubResponse {
    private long clubId;
    private String clubName;
    private String clubAddress;
    private String district;
    private String province;
    private int openTime;
    private int closeTime;
    private String hotline;
    private ClubStatus clubStatus;
    private String description;
    private AuthenticationResponse authenticationResponse;
    private List<String> urlImages;
}
