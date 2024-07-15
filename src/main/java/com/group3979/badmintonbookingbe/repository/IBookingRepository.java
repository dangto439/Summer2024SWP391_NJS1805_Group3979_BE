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

    // for Platform
    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(MONTH(t.bookingDate), SUM(t.totalPrice)) " +
            "FROM Booking t WHERE YEAR(t.bookingDate) = :year " +
            "GROUP BY MONTH(t.bookingDate) ORDER BY MONTH(t.bookingDate)")
    List<RevenueResponse> findRevenueResponseBookingByYear(@Param("year") int year);
    List<Booking> findBookingByClub_ClubId(long clubId);

    // count Booking for Club
    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(MONTH(b.bookingDate), COUNT(b.bookingId)) " +
            "FROM Booking b " +
            "WHERE b.club.clubId = :clubId " +
            "GROUP BY MONTH(b.bookingDate) " +
            "ORDER BY MONTH(b.bookingDate)")
    List<RevenueResponse> findMonthlyBookingsByClubId(@Param("clubId") Long clubId);


    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(MONTH(b.bookingDate), COUNT(b.bookingId)) " +
            "FROM Booking b " +
            "JOIN b.club c " +
            "JOIN c.account a " +
            "WHERE a.id = :accountId " +
            "GROUP BY MONTH(b.bookingDate) " +
            "ORDER BY MONTH(b.bookingDate)")
    List<RevenueResponse> findMonthlyBookingsByAccountId(@Param("accountId") Long accountId);
    @Query("SELECT b " +
            "FROM Booking  b " +
            "WHERE b.expirationStatus =' UNEXPIRED' AND b.bookingType = 'FLEXIBLEBOOKING' ")
    List<Booking> getFlexibleBookingByYear();

    @Query("SELECT  b.club\n" +
            "    FROM Booking b\n" +
            "    GROUP BY b.club\n" +
            "    ORDER BY COUNT(b.club) DESC\n" +
            "    LIMIT 10 ")
    List<Club> getTenOutstandingClubs();
}
