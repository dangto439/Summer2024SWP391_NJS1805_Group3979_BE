package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ClubRequest;
import com.group3979.badmintonbookingbe.model.response.ClubResponse;
import com.group3979.badmintonbookingbe.service.ClubService;
import com.group3979.badmintonbookingbe.service.CourtService;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class ClubAPI {
    @Autowired
    AccountUtils accountUtils;

    @Autowired
    private ClubService clubService;
    @Autowired
    CourtService courtService;

    // Get all clubs
    @GetMapping("/clubs")
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubRequests());
    }

    // Get all clubs of current account
    @GetMapping("/current-clubs")
    public ResponseEntity<List<ClubResponse>> getAllClubsCurrentAccount() {
        return ResponseEntity.ok(clubService.getAllClubRequests());
    }

    // Get club by id
    @GetMapping("/club/{id}")
    public ResponseEntity<ClubResponse> getClubById(@PathVariable Long id) {
        ClubResponse clubResponse = clubService.getClubResponseById(id);
        return ResponseEntity.ok(clubResponse);
    }

    // Create new club
    @PostMapping("/club")
    public ResponseEntity<ClubResponse> createClub(@RequestBody ClubRequest clubRequest) {
        ClubResponse club = clubService.createClub(clubRequest);
        return ResponseEntity.ok(club);
    }

    // Update existing club
    @PutMapping("/club/{id}")
    public ResponseEntity<ClubResponse> updateClub(@PathVariable Long id, @RequestBody ClubRequest club) {
        ClubResponse updatedClub = clubService.updateClub(id, club);
        return ResponseEntity.ok(updatedClub);
    }

    // Delete club
    @DeleteMapping("/club/{id}")
    public ResponseEntity<String> deleteClub(@PathVariable Long id) {
        boolean deleteSuccess = clubService.deleteStatusClub(id);
        if (deleteSuccess) {
            return ResponseEntity.ok("Deleted Successfully");
        }
        return ResponseEntity.ok("Delete failed");
    }

}
