package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Slot;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.ISlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {
    @Autowired
    private IClubRepository clubRepository;
    @Autowired
    private ISlotRepository slotRepository;

    public List<Slot> getSlotByClubId(long clubId) {
        Club club = clubRepository.findByClubId(clubId);
        List<Slot> slots = new ArrayList<>();
        if (club != null) {
            for (int i = club.getOpenTime(); i < club.getCloseTime(); i++) {
                slots.add(slotRepository.findSlotByTime(i));
            }
        }else {
            throw new CustomException("Club không tồn tại");
        }
        return slots;
    }
}
