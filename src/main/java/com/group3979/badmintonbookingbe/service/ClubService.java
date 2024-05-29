package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.ClubRequest;
import com.group3979.badmintonbookingbe.repository.IClubReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    // xử lí logic CRUD
    @Autowired
    IClubReposity clubReposity;

    // R - Read All
    public List<Club> getAllClubRequests() {
        return clubReposity.findAll();
    }

    // R - Read by ID
    public Optional<Club> getClubById(Long id) {
        return clubReposity.findById(id);
    }

    // C - Create
    public Club createClub(ClubRequest clubRequest) {
        Club club = new Club();
        club.setClubAddress(clubRequest.getClubAddress());
        club.setClubName(clubRequest.getClubName());
        club.setHotline(clubRequest.getClubhotline());

        Club createdClub = clubReposity.save(club);

        if (createdClub != null) {
            System.out.println("Club created successfully:");
            System.out.println(createdClub);
        } else {
            System.out.println("Failed to create club.");
        }

        return createdClub;
    }

    // U - Update
    public ResponseEntity<String> updateClub(Long id, ClubRequest clubRequest) {
        Optional<Club> optionalClub = clubReposity.findById(id);

        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            club.setClubAddress(clubRequest.getClubAddress());
            club.setClubName(clubRequest.getClubName());
            club.setHotline(clubRequest.getClubhotline());

            Club updatedClub = clubReposity.save(club);

            if (updatedClub != null) {
                System.out.println("Club updated successfully:");
                System.out.println(updatedClub);
                return ResponseEntity.ok("Club updated successfully");
            } else {
                System.out.println("Failed to update club.");
                return ResponseEntity.ok("Failed to update club");
            }
        } else {
            System.out.println("Club not found.");
            return ResponseEntity.notFound().build();
        }
    }

    // D - Delete
    public void deleteClub(Long id) {
        Optional<Club> optionalClub = clubReposity.findById(id);

        if (optionalClub.isPresent()) {
            clubReposity.deleteById(id);
            System.out.println("Club deleted successfully.");
        } else {
            System.out.println("Failed to delete club. Club not found.");
        }

    }

}
