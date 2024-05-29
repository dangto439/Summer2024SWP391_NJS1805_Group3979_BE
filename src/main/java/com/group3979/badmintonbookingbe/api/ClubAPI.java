package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.ClubRequest;
import com.group3979.badmintonbookingbe.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/club")
public class ClubAPI {

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
        return clubService.getClubById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new club
    @PostMapping
    public Club createClub(@RequestBody ClubRequest club) {
        return clubService.createClub(club);
    }

    // Update existing club
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody ClubRequest club) {
        try {
            Club updatedClub = clubService.updateClub(id, club);
            return ResponseEntity.ok(updatedClub);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete club
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClub(@PathVariable Long id) {
        Optional<Club> optionalClub = clubService.getClubById(id);
        if (optionalClub.isPresent()) {
            clubService.deleteClub(id);
            return ResponseEntity.ok("Delete successfully");
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
