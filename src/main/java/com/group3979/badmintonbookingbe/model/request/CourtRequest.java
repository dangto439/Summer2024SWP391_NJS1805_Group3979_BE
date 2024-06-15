package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.api.CourtAPI;
import com.group3979.badmintonbookingbe.eNum.CourtStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourtRequest {
    private CourtStatus courtStatus;
    private long courtId;
}
