package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.eNum.CourtStatus;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Court;
import com.group3979.badmintonbookingbe.model.request.CourtRequest;
import com.group3979.badmintonbookingbe.model.response.CourtResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.ICourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourtService {
    @Autowired
    ICourtRepository courtRepository;
    @Autowired
    IClubRepository clubRepository;

    // get all courts of a club
    public List<CourtResponse> getAllCourtsByClub(long clubId) {
        Club club = clubRepository.findByClubId(clubId);
        List<Court> courts = courtRepository.findByClub(club);
        List<CourtResponse> courtResponses = new ArrayList<>();
        for (Court court : courts) {
            CourtResponse courtResponse = new CourtResponse();
            courtResponse.setCourtId(court.getCourtId());
            courtResponse.setCourtName(court.getCourtName());
           courtResponse.setCourtStatus(court.getCourtStatus());
            courtResponses.add(courtResponse);
        }
        return courtResponses;
    }

    public Court getCourtById(long id) {
        return courtRepository.findByCourtId(id);
    }

    // create courts by quantity of a Club when creating club
    public List<Court> createCourtsByClub(Club club, int quantityCourts) {
        List<Court> courts = new ArrayList<Court>();
        for (int i = 1; i <= quantityCourts; i++) {
            Court court = new Court();
            court.setClub(club);
            court.setCourtName("Sân số " + i);
            court.setCourtStatus(CourtStatus.ACTIVE);
            court = courtRepository.save(court);
            courts.add(court);
        }
        return courts;
    }
    public CourtResponse createCourtByClubId(Long Id) {
        Club club = clubRepository.findByClubId(Id);
        List<Court> courts = courtRepository.findByClub(club);
        Court court = new Court();
        court.setClub(club);
        court.setCourtName("Sân số " + (courts.size() + 1));
        court.setCourtStatus(CourtStatus.ACTIVE);
        court = courtRepository.save(court);
        CourtResponse courtResponse = new CourtResponse();
        courtResponse.setCourtId(court.getCourtId());
        courtResponse.setCourtName(court.getCourtName());
        courtResponse.setCourtStatus(court.getCourtStatus());
        return courtResponse;
    }

    public boolean inactiveCourtStatus(long id) {
        Court court = courtRepository.findByCourtId(id);
        if (court != null) {
            court.setCourtStatus(CourtStatus.INACTIVE);
            courtRepository.save(court);
            return true;
        }
        return false;
    }
    public CourtResponse changeCourtStatus(CourtRequest courtRequest){
            Court court = courtRepository.findByCourtId(courtRequest.getCourtId());
            court.setCourtStatus(courtRequest.getCourtStatus());
            courtRepository.save(court);
            CourtResponse courtResponse = new CourtResponse();
            courtResponse.setCourtId(court.getCourtId());
            courtResponse.setCourtName(court.getCourtName());
            courtResponse.setCourtStatus(court.getCourtStatus());
            return courtResponse;
    }
}
