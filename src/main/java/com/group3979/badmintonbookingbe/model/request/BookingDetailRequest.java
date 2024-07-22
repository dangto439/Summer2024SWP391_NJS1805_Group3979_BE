package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingDetailRequest {
    private Long CourtSlotId;
    private LocalDate playingDate;
}
