package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.request.ClubRequest;
import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import com.group3979.badmintonbookingbe.model.response.ClubResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClubService {

    // xử lí logic CRUD
    @Autowired
    IClubRepository clubRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    IAuthenticationRepository authenticationRepository;

    @Autowired
    CourtService courtService;

    // R - Read All
    public List<ClubResponse> getAllClubRequests() {
        List<Club> clubs = clubRepository.findAll();
        List<ClubResponse> clubResponses = new ArrayList<>();
        for (Club club : clubs) {
            clubResponses.add(getClubResponseById(club.getClubId()));
        }
        return clubResponses;
    }

    // R - Read by ID
    // public Club getClubById(Long id) {
    // return clubRepository.findByClubId(id);
    // }

    public ClubResponse getClubResponseById(Long id) {
        Club club = clubRepository.findByClubId(id);
        Account account = club.getAccount();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .email(account.getEmail())
                .role(account.getRole())
                .phone(account.getPhone())
                .name(account.getName())
                .gender(account.getGender())
                .accountStatus(account.getAccountStatus())
                .build();
        return ClubResponse.builder()
                .clubName(club.getClubName())
                .clubStatus(club.getClubStatus())
                .clubAddress(club.getClubAddress())
                .hotline(club.getHotline())
                .closeTime(club.getCloseTime())
                .openTime(club.getOpenTime())
                .clubId(club.getClubId())
                .description(club.getDescription())
                .authenticationResponse(authenticationResponse).build();
    }

    // C - Create
    public ClubResponse createClub(ClubRequest clubRequest) {
        Club club = new Club();
        club.setClubAddress(clubRequest.getClubAddress());
        club.setClubName(clubRequest.getClubName());
        club.setHotline(clubRequest.getClubhotline());
        club.setOpenTime(clubRequest.getOpeningTime());
        club.setCloseTime(clubRequest.getClosingTime());
        club.setDescription(clubRequest.getClubDescription());
        club.setClubStatus(ClubStatus.ACTIVE);
        club.setAccount(accountUtils.getCurrentAccount());

        club = clubRepository.save(club);
        courtService.createCourtsByClub(club, clubRequest.getCapacity());
        return getClubResponseById(club.getClubId());
    }

    // U - Update
    public ClubResponse updateClub(Long id, ClubRequest clubRequest) {
        Club club = clubRepository.findByClubId(id);

        if (club != null) {
            club.setClubAddress(clubRequest.getClubAddress());
            club.setClubName(clubRequest.getClubName());
            club.setHotline(clubRequest.getClubhotline());
            club.setOpenTime(clubRequest.getOpeningTime());
            club.setCloseTime(clubRequest.getClosingTime());
            club.setDescription(clubRequest.getClubDescription());
            club = clubRepository.save(club);
            return getClubResponseById(club.getClubId());
        }
        return null;
    }

    // D - Delete
    public boolean deleteStatusClub(Long id) {
        Club club = clubRepository.findByClubId(id);
        club.setClubStatus(ClubStatus.DELETED);
        clubRepository.save(club);
        if (club.getClubStatus() == ClubStatus.DELETED) {
            return true;
        }
        return false;
    }

}
