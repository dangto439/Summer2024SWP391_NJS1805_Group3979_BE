package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.ClubSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClubSlotRepository extends JpaRepository<ClubSlot, Long> {
    @Query("SELECT a FROM ClubSlot a WHERE a.club = :club")
    List<ClubSlot> findClubSlotByClubId(@Param("club") Club club);
}
