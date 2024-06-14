package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.entity.Booking;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DailyBookingRequest {
    List<BookingDetailRequest> bookingDetailRequests;
    private String promotionCode;
    private long flexibleBookingId;
}
