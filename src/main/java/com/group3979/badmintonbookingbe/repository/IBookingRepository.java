package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Booking;
import com.group3979.badmintonbookingbe.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookingId(Long bookingId);
    List<Booking> findBookingByAccountAndClub(Account account, Club club);
}
