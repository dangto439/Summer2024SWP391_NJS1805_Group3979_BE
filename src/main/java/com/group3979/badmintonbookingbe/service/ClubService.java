package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.ClubRequest;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    // xử lí logic CRUD
    @Autowired
    IClubRepository clubRepository;

    // R - Read All
    public List<Club> getAllClubRequests() {
        return clubRepository.findAll();
    }

    // R - Read by ID
    public Club getClubById(Long id) {
        return clubRepository.findByClubId(id);
    }

    // C - Create
    public Club createClub(ClubRequest clubRequest) {
        Club club = new Club();
        club.setClubAddress(clubRequest.getClubAddress());
        club.setClubName(clubRequest.getClubName());
        club.setHotline(clubRequest.getClubhotline());
        club.setClubStatus(ClubStatus.ACTIVE);
        return clubRepository.save(club);
    }

    // U - Update
    public Club updateClub(Long id, ClubRequest clubRequest) {
        Club club = clubRepository.findByClubId(id);

        if (club != null) {
            club.setClubAddress(clubRequest.getClubAddress());
            club.setClubName(clubRequest.getClubName());
            club.setHotline(clubRequest.getClubhotline());
            return clubRepository.save(club);
        }
         return null;
    }

    // D - Delete
    public boolean deleteStatusClub(Long id) {
        Club optionalClub = clubRepository.findByClubId(id);
        optionalClub.setClubStatus(ClubStatus.DELETED);
        clubRepository.save(optionalClub);
        if (optionalClub.getClubStatus() == ClubStatus.DELETED) {
            return true;
        }
        return false;
    }

}
