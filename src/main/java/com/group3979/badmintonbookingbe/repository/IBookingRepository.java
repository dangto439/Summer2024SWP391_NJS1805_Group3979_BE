package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Booking;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.response.RevenueResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookingId(Long bookingId);
    List<Booking> findBookingByAccountAndClub(Account account, Club club);
    List<Booking> findBookingByAccount(Account account);

    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(MONTH(t.bookingDate), SUM(t.totalPrice)) " +
            "FROM Booking t WHERE YEAR(t.bookingDate) = :year " +
            "GROUP BY MONTH(t.bookingDate) ORDER BY MONTH(t.bookingDate)")
    List<RevenueResponse> findRevenueResponseBookingByYear(@Param("year") int year);
    List<Booking> findBookingByClub_ClubId(long clubId);
}
