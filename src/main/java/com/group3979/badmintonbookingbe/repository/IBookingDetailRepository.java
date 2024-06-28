package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IBookingDetailRepository extends JpaRepository<BookingDetail, Long> {
    BookingDetail findBookingDetailByBookingDetailId(long bookingDetailId);
    List<BookingDetail> findBookingDetailByBooking_BookingId(long booking_bookingId);
    BookingDetail findBookingDetailByCheckInCode(String checkInCode);
}
