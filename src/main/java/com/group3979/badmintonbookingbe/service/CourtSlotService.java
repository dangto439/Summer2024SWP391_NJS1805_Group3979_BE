package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.CourtSlotStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.AuthException;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.CourtSlotRequest;
import com.group3979.badmintonbookingbe.model.response.CourtResponse;
import com.group3979.badmintonbookingbe.model.response.CourtSlotResponse;
import com.group3979.badmintonbookingbe.model.response.CourtSlotStatusResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.ICourtRepository;
import com.group3979.badmintonbookingbe.repository.ICourtSlotRepository;

import com.group3979.badmintonbookingbe.repository.ISlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public void createEachCourtSlot(Club club, Court newCourt) {
        Court court = courtRepository.findByClub(club).get(0);
        for (int i = 1; i <= 24; i++) {
            CourtSlot courtSlot = new CourtSlot();
            Slot slot = slotRepository.findSlotByTime(i);
            courtSlot.setCourt(newCourt);
            courtSlot.setSlot(slot);
            for (CourtSlot courtSlot1 : court.getCourtSlots()) {
                if(courtSlot.getSlot().getTime() == courtSlot1.getSlot().getTime()) {
                    courtSlot.setPrice(courtSlot1.getPrice());
                    break;
                }
            }
            courtSlotRepository.save(courtSlot);
        }


    }

    public List<CourtSlotResponse> createCourtSlot(Long clubId, CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();
        Club club = clubRepository.findByClubId(clubId);
        List<Court> courtList = courtRepository.findByClub(club);

        for (int start = 1, end = 24; start <= end; start++) {
            for (Court court : courtList) {
                if (courtSlotRepository.existsByCourtAndSlot_Time(court, start)) continue;
                CourtSlot courtSlot = new CourtSlot();
                Slot slot = slotRepository.findSlotByTime(start);

                if (start >= courtSlotRequest.getRushHourRequest().getStartTime() &&
                        start < courtSlotRequest.getRushHourRequest().getEndTime()) {
                    courtSlot.setPrice(courtSlotRequest.getRushHourRequest().getRushPrice());
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
                        .price(courtSlotRequest.getRushHourRequest().getRushPrice())
                        .courtResponse(courtResponse)
                        .clubId(clubId)
                        .slotId(courtSlot.getSlot().getSlotId())
                        .build();

                courtSlotResponses.add(courtSlotResponse);
            }
        }
        if (courtSlotResponses.isEmpty()) throw new AuthException("Đã thêm đủ slot của sân");
        return courtSlotResponses;
    }


    public List<CourtSlotResponse> getAllCourtSlots(Long courtId) {
        List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();
        Court court = courtRepository.findByCourtId(courtId);
        Club club = clubRepository.findByClubId(court.getClub().getClubId());

        List<CourtSlot> courtSlots = courtSlotRepository.findByCourt(court);

        for (CourtSlot courtSlot : courtSlots) {
            if (courtSlot.getSlot().getTime() >= club.getOpenTime() && courtSlot.getSlot().getTime() < club.getCloseTime()) {
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
        }
        return courtSlotResponses;
    }

    public float getDefaultPriceByClub(Club club) {
        return courtSlotRepository.findPriceByCourt(club.getCourts().get(0));
    }

    public List<CourtSlotResponse> updateCourtSlot(Long clubId, CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();
        Club club = clubRepository.findByClubId(clubId);
        List<Court> courtList = courtRepository.findByClub(club);

        for (Court court : courtList) {
            List<CourtSlot> courtSlotList = courtSlotRepository.findByCourt(court);
            for (CourtSlot courtSlot : courtSlotList) {
                if (courtSlot.getSlot().getTime() >= courtSlotRequest.getRushHourRequest().getStartTime() &&
                        courtSlot.getSlot().getTime() < courtSlotRequest.getRushHourRequest().getEndTime()) {
                    courtSlot.setPrice(courtSlotRequest.getRushHourRequest().getRushPrice());
                } else {
                    courtSlot.setPrice(courtSlotRequest.getPrice());
                }
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
                        .clubId(clubId)
                        .slotId(courtSlot.getSlot().getSlotId())
                        .build();

                courtSlotResponses.add(courtSlotResponse);
            }
        }
        return courtSlotResponses;
    }

    public List<CourtSlotResponse> existCourtSlotInADay(LocalDate playingDate, long courtId) {
        Court court = courtRepository.findByCourtId(courtId);
        if (court != null) {
            List<CourtSlot> courtSlots =
                    courtSlotRepository.findCourtSlotByPlayingDate(playingDate, court);

            List<CourtSlotResponse> courtSlotResponses = new ArrayList<>();
            for (CourtSlot courtSlot : courtSlots) {
                CourtSlotResponse courtSlotResponse = this.getCourtSlotResponse(courtSlot);
                courtSlotResponses.add(courtSlotResponse);
            }
            return courtSlotResponses;
        } else {
            throw new CustomException("Sân không tồn tại");
        }
    }

    public List<CourtSlotStatusResponse> getCourtSlotByCourtId(LocalDate playingDate, long courtId) {
        Court court = courtRepository.findByCourtId(courtId);
        List<CourtSlotStatusResponse> courtSlotStatusResponses = new ArrayList<>();
        if (court != null) {

            List<CourtSlot> courtSlotsExisted =
                    courtSlotRepository.findCourtSlotByPlayingDate(playingDate, court);
            List<CourtSlot> courtSlots = courtSlotRepository.findByCourt(court);
            courtSlots.removeIf(courtSlot -> (courtSlot.getSlot().getTime() < court.getClub().getOpenTime()) ||
                    (courtSlot.getSlot().getTime() >= court.getClub().getCloseTime()));
            for (CourtSlot courtSlot : courtSlots) {
                CourtSlotStatusResponse courtSlotStatusResponse = new CourtSlotStatusResponse();
                courtSlotStatusResponse.setCourtSlotId(courtSlot.getCourtSlotId());
                courtSlotStatusResponse.setPrice(courtSlot.getPrice());
                courtSlotStatusResponse.setSlotId(courtSlot.getSlot().getSlotId());
                if (courtSlotsExisted.contains(courtSlot)) {
                    courtSlotStatusResponse.setCourtSlotStatus(CourtSlotStatus.INACTIVE);
                } else {
                    courtSlotStatusResponse.setCourtSlotStatus(CourtSlotStatus.ACTIVE);
                }
                courtSlotStatusResponses.add(courtSlotStatusResponse);
            }
            return courtSlotStatusResponses;
        } else {
            throw new CustomException("Sân không tồn tại");
        }
    }


    public CourtSlotResponse getCourtSlotResponse(CourtSlot courtSlot) {
        Court court = courtSlot.getCourt();
        CourtResponse courtResponse = CourtResponse.builder()
                .courtId(court.getCourtId())
                .courtStatus(court.getCourtStatus())
                .courtName(court.getCourtName())
                .build();
        return CourtSlotResponse.builder()
                .courtSlotId(courtSlot.getCourtSlotId())
                .price(courtSlot.getPrice())
                .courtResponse(courtResponse)
                .clubId(court.getClub().getClubId())
                .slotId(courtSlot.getSlot().getSlotId())
                .build();
    }
}
