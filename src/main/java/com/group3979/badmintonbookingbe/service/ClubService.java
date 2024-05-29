package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.AuthenticationResponse;
import com.group3979.badmintonbookingbe.model.ClubRequest;
import com.group3979.badmintonbookingbe.model.ClubResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    // xử lí logic CRUD
    @Autowired
    IClubRepository clubRepository;

    @Autowired
    AccountUtils accountUtils;

    // R - Read All
    public List<Club> getAllClubRequests() {
        return clubRepository.findAll();
    }

    // R - Read by ID
    public Club getClubById(Long id) {
        return clubRepository.findByClubId(id);
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

        Account account = accountUtils.getCurrentAccount();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .email(account.getEmail())
                .role(account.getRole())
                .phone(account.getPhone())
                .name(account.getName())
                .gender(account.getGender())
                .accountStatus(account.getAccountStatus())
                .build();
        club = clubRepository.save(club);

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

    // U - Update
    public Club updateClub(Long id, ClubRequest clubRequest) {
        Club club = clubRepository.findByClubId(id);

        if (club != null) {
            club.setClubAddress(clubRequest.getClubAddress());
            club.setClubName(clubRequest.getClubName());
            club.setHotline(clubRequest.getClubhotline());
            club.setOpenTime(clubRequest.getOpeningTime());
            club.setCloseTime(clubRequest.getClosingTime());
            club.setDescription(clubRequest.getClubDescription());
            return clubRepository.save(club);
        }
        return null;
    }

    // D - Delete
    public boolean deleteStatusClub(Long id) {
        Club club = clubRepository.findByClubId(id);
        club.setClubStatus(ClubStatus.DELETED);
        clubRepository.save(club);
        if(club.getClubStatus() == ClubStatus.DELETED) {
            return true;
        }
        return false;
    }

}
