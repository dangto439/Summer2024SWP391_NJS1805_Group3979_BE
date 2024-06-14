package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookingId(Long bookingId);
}
