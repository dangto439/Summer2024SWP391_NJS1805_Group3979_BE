package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.CourtRequest;
import com.group3979.badmintonbookingbe.model.response.CourtResponse;
import com.group3979.badmintonbookingbe.service.CourtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class CourtAPI {
    @Autowired
    CourtService courtService;

    // Create a new court
    @PostMapping("/court/{clubId}")
    public ResponseEntity<CourtResponse> createCourtByClubId(@PathVariable Long clubId) {
        CourtResponse court = courtService.createCourtByClubId(clubId);
        return ResponseEntity.ok(court);
    }

    // Get all courts by ClubId
    @GetMapping("/courts/{clubId}")
    public ResponseEntity<List<CourtResponse>> getCourtByClubId(@PathVariable Long clubId) {
        List<CourtResponse> courts = courtService.getAllCourtsByClub(clubId);
        return ResponseEntity.ok(courts);
    }
    @PutMapping("/court/{id}")
    public ResponseEntity<String> inactiveCourtStatus(@PathVariable Long id) {
        boolean result = courtService.inactiveCourtStatus(id);
        if (result) {
            return ResponseEntity.ok("Deleted Successfully");
        }
        return ResponseEntity.ok("Delete failed");
    }
    @PutMapping("/court/delete/{id}")
    public ResponseEntity<CourtResponse> changeCourtStatus(@RequestBody CourtRequest courtRequest) {
        CourtResponse courtResponse = courtService.changeCourtStatus(courtRequest);
        return ResponseEntity.ok(courtResponse);
    }
}
