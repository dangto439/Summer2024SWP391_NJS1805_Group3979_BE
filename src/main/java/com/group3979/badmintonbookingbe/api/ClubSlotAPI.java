package com.group3979.badmintonbookingbe.api;


import com.group3979.badmintonbookingbe.entity.ClubSlot;
import com.group3979.badmintonbookingbe.service.ClubSlotService;
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
public class ClubSlotAPI {
    @Autowired
    ClubSlotService clubSlotService;

    @GetMapping("/club-slot/{id}")
    public ResponseEntity getClubSlots(@PathVariable Long id){ // id == clubId
        try {
            List<ClubSlot> clubSlotList = clubSlotService.getClubSlots(id);
            return ResponseEntity.ok(clubSlotList);
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
