package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private long bookingId;
    private LocalDateTime bookingDate;
    private double totalPrice;
    private double temporaryPrice;
    private double discountPrice;
    private BookingType bookingType;
    private int amountTime;
    private ExpirationStatus expirationStatus;
    private long ClubId;
    private String ClubName;
    private BookingStatus bookingStatus;
    private long customerId;
    List<BookingDetailResponse> bookingDetailResponseList;
}
