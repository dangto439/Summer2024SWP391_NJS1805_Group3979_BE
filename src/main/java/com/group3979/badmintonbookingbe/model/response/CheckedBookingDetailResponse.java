package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckedBookingDetailResponse {
  private String customerName;
  private LocalDate playingDate;
  private String phone;
  private BookingStatus bookingStatus;
  private String courtName;
  private int timeSlot;
  private String checkInCode;
}
