package com.group3979.badmintonbookingbe.repository;


import com.group3979.badmintonbookingbe.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISlotRepository extends JpaRepository<Slot, Long> {
    Slot findSlotByTime(int time);
    Slot findSlotBySlotId(long id);


}
