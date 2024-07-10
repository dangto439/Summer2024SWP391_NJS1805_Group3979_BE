package com.group3979.badmintonbookingbe.api;


import com.group3979.badmintonbookingbe.entity.Slot;
import com.group3979.badmintonbookingbe.model.request.CourtSlotRequest;
import com.group3979.badmintonbookingbe.model.response.CourtSlotResponse;
import com.group3979.badmintonbookingbe.model.response.CourtSlotStatusResponse;
import com.group3979.badmintonbookingbe.service.CourtSlotService;
import com.group3979.badmintonbookingbe.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class CourtSlotAPI {
    @Autowired
    CourtSlotService courtSlotService;

    @Autowired
    private SlotService slotService;

    @PostMapping("/court-slot/{clubId}")
    public ResponseEntity createCourtSlot(@PathVariable Long clubId, @RequestBody CourtSlotRequest courtSlotRequest) {
//      List<CourtSlotResponse> courtSlotList = courtSlotService.createCourtSlot(clubId, courtSlotRequest);
        courtSlotService.createCourtSlot(clubId, courtSlotRequest);
        return ResponseEntity.ok("Đã tạo thành công");
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
    @GetMapping("/club/slots")
    public ResponseEntity<List<Slot>> getSlotByClubId(@RequestParam long clubId)  {
        List<Slot> slots = slotService.getSlotByClubId(clubId);
        return ResponseEntity.ok(slots);
    }
    @GetMapping("/court-slot/status")
    public ResponseEntity<List<CourtSlotStatusResponse>>  getCourtSlotStatus(@RequestParam String date,
                                                                             @RequestParam long courtId) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);;

        List<CourtSlotStatusResponse> courtSlotList = courtSlotService.getCourtSlotByCourtId(localDate,courtId);
        return ResponseEntity.ok(courtSlotList);
    }
    @GetMapping("/court-slot/exist")
    public ResponseEntity existCourtSlot(@RequestParam String date, @RequestParam long courtId) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        List<CourtSlotResponse> courtSlotList = courtSlotService.existCourtSlotInADay(localDate,courtId);
        return ResponseEntity.ok(courtSlotList);
    }
}
