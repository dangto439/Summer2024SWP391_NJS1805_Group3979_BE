package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.CourtStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourtResponse {
    private long courtId;
    private CourtStatus courtStatus;
    private String courtName;
}
