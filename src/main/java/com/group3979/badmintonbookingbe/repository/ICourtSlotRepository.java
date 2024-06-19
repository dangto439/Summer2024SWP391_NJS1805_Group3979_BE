package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Court;
import com.group3979.badmintonbookingbe.entity.CourtSlot;
import com.group3979.badmintonbookingbe.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ICourtSlotRepository extends JpaRepository<CourtSlot, Long> {
    boolean existsByCourtAndSlot_Time(Court court, int time);

    List<CourtSlot> findByCourt(Court court);
    CourtSlot findCourtSlotByCourtSlotId(Long id);

    @Query("SELECT a \n" +
            "FROM CourtSlot a \n" +
            "WHERE a.slot = :slot \n" +
            "AND a.court = :court ")
    CourtSlot findCourtSlotByCourtAndSlot( @Param("court") Court court, @Param("slot") Slot slot);

    @Query("SELECT a.price \n" +
            "FROM CourtSlot a \n" +
            "WHERE  a.court=:court\n" +
            "ORDER BY a.price ASC\n" +
            "LIMIT 1")
    float findPriceByCourt(@Param("court") Court court);


    @Query("SELECT a \n" +
            "FROM CourtSlot a \n" +
            "WHERE a.slot = :slot \n" +
            "AND a.court = :court \n" +
            "AND a IN (SELECT b.courtSlot\n" +
            "FROM BookingDetail b\n" +
            "WHERE b.status = 'UNFINISHED' AND b.playingDate =:playingDate )")
    CourtSlot findCourtSlotByPlayingDateAndSlot(@Param("playingDate") Date playingDate,
                                                      @Param("court") Court court, @Param("slot") Slot slot);
    @Query("SELECT a \n" +
            "FROM CourtSlot a \n" +
            "WHERE a.court = :court \n" +
            "AND a IN (SELECT b.courtSlot\n" +
            "FROM BookingDetail b\n" +
            "WHERE b.status = 'UNFINISHED' AND b.playingDate =:playingDate )")
    List<CourtSlot> findCourtSlotByPlayingDate(@Param("playingDate") Date playingDate,
                                                @Param("court") Court court);
}


