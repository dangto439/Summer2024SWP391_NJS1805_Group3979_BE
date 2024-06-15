package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IBookingDetailRepository extends JpaRepository<BookingDetail, Long> {

//    @Query("SELECT a.courtSlot \n" +
//            "FROM  BookingDetail a \n" +
//            "WHERE  a.playingDate=:playingDate\n " +
//            "AND a.status != 'CANCELLED' " +
//            "AND a.courtSlot.court = :court")
//    List<CourtSlot> findCourtSlotByPlayingDate(@Param("playingDate") Date playingDate,
//                                               @Param("court") Court court);



}
