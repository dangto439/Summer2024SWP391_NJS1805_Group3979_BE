package com.group3979.badmintonbookingbe.api;


import com.group3979.badmintonbookingbe.model.request.CourtSlotRequest;
import com.group3979.badmintonbookingbe.model.response.CourtSlotResponse;
import com.group3979.badmintonbookingbe.service.CourtSlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class CourtSlotAPI {
    @Autowired
    CourtSlotService courtSlotService;

    @PostMapping("/court-slot/{clubId}")
    public ResponseEntity createCourtSlot(@PathVariable Long clubId, @RequestBody CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotList = courtSlotService.createCourtSlot(clubId, courtSlotRequest);
        return ResponseEntity.ok(courtSlotList);
    }

    @GetMapping("/court-slot/{courtId}")
    public ResponseEntity getCourtSlots(@PathVariable Long courtId) {
        List<CourtSlotResponse> courtSlotList = courtSlotService.getAllCourtSlots(courtId);
        return ResponseEntity.ok(courtSlotList);
    }

    @PutMapping("/court-slot/{clubId}")
    public ResponseEntity updateCourtSlot(@PathVariable Long clubId, @RequestBody CourtSlotRequest courtSlotRequest) {
        List<CourtSlotResponse> courtSlotList = courtSlotService.updateCourtSlot(clubId, courtSlotRequest);
        return ResponseEntity.ok(courtSlotList);
    }
    @GetMapping("/court-slot/exist")
    public ResponseEntity existCourtSlot(@RequestParam String date, @RequestParam long courtId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = formatter.parse(date);

        List<CourtSlotResponse> courtSlotList = courtSlotService.existCourtSlotInADay(parsedDate,courtId);
        return ResponseEntity.ok(courtSlotList);
    }
}
