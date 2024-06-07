package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Court;
import com.group3979.badmintonbookingbe.entity.CourtSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICourtSlotRepository extends JpaRepository<CourtSlot, Long> {
    boolean existsByCourtAndSlot_Time(Court court, int time);
    List<CourtSlot> findByCourt(Court court);
}

