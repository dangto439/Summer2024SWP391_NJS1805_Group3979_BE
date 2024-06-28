package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailResponse {
    private long bookingDetailId;
    private LocalDate playingDate;
    private double price;
    private BookingDetailStatus status;
    private long bookingId;
    private String checkInCode;
    private long CourtSlotId;
    private int timeSlot;
}
