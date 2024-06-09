package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.AuthException;
import com.group3979.badmintonbookingbe.model.request.CourtSlotRequest;
import com.group3979.badmintonbookingbe.model.response.CourtResponse;
import com.group3979.badmintonbookingbe.model.response.CourtSlotResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.ICourtRepository;
import com.group3979.badmintonbookingbe.repository.ICourtSlotRepository;

import com.group3979.badmintonbookingbe.repository.ISlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourtSlotService {
    @Autowired
    ICourtSlotRepository courtSlotRepository;
    @Autowired
    ICourtRepository courtRepository;
    @Autowired
    IClubRepository clubRepository;
    @Autowired
    ISlotRepository slotRepository;

    public List<CourtSlotResponse> createCourtSlot(CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();
        Club club = clubRepository.findByClubId(courtSlotRequest.getClubId());
        List<Court> courtsList = courtRepository.findByClub(club);


        for (int start = club.getOpenTime(), end = club.getCloseTime(); start < end; start++) {
            for (Court court : courtsList) {
                if (courtSlotRepository.existsByCourtAndSlot_Time(court, start)) continue;
                CourtSlot courtSlot = new CourtSlot();
                Slot slot = slotRepository.findSlotByTime(start);

                if (start >= courtSlotRequest.getRushHourRequest().getStartTime()
                        && start < courtSlotRequest.getRushHourRequest().getEndTime()) {
                    courtSlot.setPrice(courtSlotRequest.getPrice() * 120 / 100); // increase 20% in rush_hours
                } else {
                    courtSlot.setPrice(courtSlotRequest.getPrice());
                }
                courtSlot.setSlot(slot);
                courtSlot.setCourt(court);
                courtSlot = courtSlotRepository.save(courtSlot);

                CourtResponse courtResponse = CourtResponse.builder()
                        .courtId(court.getCourtId())
                        .courtStatus(court.getCourtStatus())
                        .courtName(court.getCourtName())
                        .build();

                CourtSlotResponse courtSlotResponse = CourtSlotResponse.builder()
                        .courtSlotId(courtSlot.getCourtSlotId())
                        .price(courtSlot.getPrice())
                        .courtResponse(courtResponse)
                        .clubId(club.getClubId())
                        .slotId(courtSlot.getSlot().getSlotId())
                        .build();

                courtSlotResponses.add(courtSlotResponse);
            }
        }
        //
        if (courtSlotResponses.isEmpty()) throw new AuthException("Đã thêm đủ slot của sân");
        return courtSlotResponses;
    }

    public List<CourtSlotResponse> getAllCourtSlots(Long courtId) {
        Court court = courtRepository.findByCourtId(courtId);

        List<CourtSlot> courtSlots = courtSlotRepository.findByCourt(court);
        List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();

        for (CourtSlot courtSlot : courtSlots) {
            CourtResponse courtResponse = CourtResponse.builder()
                    .courtId(court.getCourtId())
                    .courtName(court.getCourtName())
                    .courtStatus(court.getCourtStatus())
                    .build();

            CourtSlotResponse courtSlotResponse = CourtSlotResponse.builder()
                    .courtSlotId(courtSlot.getCourtSlotId())
                    .price(courtSlot.getPrice())
                    .courtResponse(courtResponse)
                    .clubId(court.getClub().getClubId())
                    .slotId(courtSlot.getSlot().getSlotId())
                    .build();

            courtSlotResponses.add(courtSlotResponse);
        }
        return courtSlotResponses;
    }
}
