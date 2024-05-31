package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.CourtResponse;
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
    //Get all courts by ClubId
    @GetMapping("/courts/{clubId}")
    public ResponseEntity<List<CourtResponse>> getCourtByClubId(@PathVariable Long clubId) {
        List<CourtResponse> courts = courtService.getAllCourtsByClub(clubId);
        return ResponseEntity.ok(courts);
    }
}
