package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.CourtSlotStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourtSlotStatusResponse {
    private long courtSlotId;
    private CourtSlotStatus courtSlotStatus;
    private long slotId;
     private double price;
}
