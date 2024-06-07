package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.ClubSlot;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IClubSlotRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClubSlotService {
    @Autowired
    IClubSlotRepository clubSlotRepository;

    @Autowired
    IClubRepository clubRepository;

    public List<ClubSlot> createClubSlot(Long clubId, float priceOfClub, int startTime, int endTime) {
        List<ClubSlot> clubSlots = new ArrayList<>();
        Club club = clubRepository.findByClubId(clubId);
        for (int start = club.getOpenTime(), end = club.getCloseTime(); start < end; start++) {
            ClubSlot clubSlot = new ClubSlot();
            clubSlot.setTime(start);
            if (start >= startTime && start < endTime) {
                clubSlot.setPrice(priceOfClub * 120 / 100);
            } else {
                clubSlot.setPrice(priceOfClub); // hardcode increase 20% in rush_hours
            }
            clubSlot.setClub(club);
            clubSlot = clubSlotRepository.save(clubSlot);
            clubSlots.add(clubSlot);
        }
        return clubSlots;
    }

    public List<ClubSlot> getClubSlots(Long clubId) throws BadRequestException {
        List<ClubSlot> clubSlots;

        Club club = clubRepository.findByClubId(clubId);
        if(club == null){
            throw new BadRequestException("Club not found");
        }
        clubSlots = clubSlotRepository.findClubSlotByClubId(club);
        return clubSlots;
    }
}
