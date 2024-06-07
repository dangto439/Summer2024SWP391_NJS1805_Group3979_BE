package com.group3979.badmintonbookingbe.api;


import com.group3979.badmintonbookingbe.entity.CourtSlot;
import com.group3979.badmintonbookingbe.model.request.CourtSlotRequest;
import com.group3979.badmintonbookingbe.model.response.CourtSlotResponse;
import com.group3979.badmintonbookingbe.service.CourtSlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class CourtSlotAPI {
    @Autowired
    CourtSlotService courtSlotService;

    @PostMapping("/court-slot")
    public ResponseEntity createCourtSlot(@RequestBody CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotList = courtSlotService.createCourtSlot(courtSlotRequest);
        return ResponseEntity.ok(courtSlotList);
    }

    @GetMapping("/court-slot/{courtId}")
    public ResponseEntity getCourtSlots(@PathVariable Long courtId) {
        List<CourtSlotResponse> courtSlotList = courtSlotService.getAllCourtSlots(courtId);
        return ResponseEntity.ok(courtSlotList);
    }
}
