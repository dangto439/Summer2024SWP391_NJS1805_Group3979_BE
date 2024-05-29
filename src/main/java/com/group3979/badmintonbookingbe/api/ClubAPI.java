package com.group3979.badmintonbookingbe.api;


import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.AccountReponse;
import com.group3979.badmintonbookingbe.model.ClubRequest;
import com.group3979.badmintonbookingbe.model.ClubResponse;
import com.group3979.badmintonbookingbe.service.ClubService;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/club")
@SecurityRequirement(name = "api")
public class ClubAPI {
    @Autowired
    AccountUtils accountUtils;

    @Autowired
    private ClubService clubService;

    // Get all clubs
    @GetMapping
    public List<Club> getAllClubs() {
        return clubService.getAllClubRequests();
    }

    // Get club by id
    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        Club club = clubService.getClubById(id);
        return ResponseEntity.ok(club);
    }



    // Create new club
    @PostMapping
    public ResponseEntity<ClubResponse> createClub(@RequestBody ClubRequest clubRequest) {
        ClubResponse club =  clubService.createClub(clubRequest);
        return ResponseEntity.ok(club);
    }

    //Update existing club
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody ClubRequest club) {
           Club updatedClub = clubService.updateClub(id, club);
            return ResponseEntity.ok(updatedClub);
    }

    // Delete club
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClub(@PathVariable Long id) {
      boolean deleteSuccess =  clubService.deleteStatusClub(id);
      if(deleteSuccess) {
          return ResponseEntity.ok("Deleted Successfully");
      }
      return ResponseEntity.ok("Delete failed");
    }
}


