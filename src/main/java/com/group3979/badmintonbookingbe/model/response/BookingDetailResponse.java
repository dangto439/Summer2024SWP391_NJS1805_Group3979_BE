package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.entity.Booking;
import com.group3979.badmintonbookingbe.entity.CourtSlot;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailResponse {
    private long bookingDetailId;
    private Date playingDate;
    private float price;
    private BookingDetailStatus status;
    private long bookingId;
    private String checkInCode;
    private long CourtSlotId;
}
