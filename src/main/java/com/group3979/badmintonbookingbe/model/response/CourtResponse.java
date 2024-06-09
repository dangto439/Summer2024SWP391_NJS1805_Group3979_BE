package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.CourtStatus;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtResponse {
    private long courtId;
    private CourtStatus courtStatus;
    private String courtName;
}
