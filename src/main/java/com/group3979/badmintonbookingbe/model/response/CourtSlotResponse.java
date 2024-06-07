package com.group3979.badmintonbookingbe.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourtSlotResponse {
    private Long courtSlotId;
    private float price;
    CourtResponse courtResponse;
    private Long clubId;
}
