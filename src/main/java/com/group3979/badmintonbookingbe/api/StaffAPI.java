package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.StaffRegisterRequest;
import com.group3979.badmintonbookingbe.model.response.StaffResponse;
import com.group3979.badmintonbookingbe.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class StaffAPI {
    @Autowired
    AuthenticationService authenticationService;

    // getStaffLists of Club-Owner
    @GetMapping("/staff")
    public List<StaffResponse> getStaffs() {
        return authenticationService.getAllStaffs();
    }

    // getStaffList of Club
    @GetMapping("/staff/{clubId}")
    public List<StaffResponse> getStaff(@PathVariable Long clubId) {
        return authenticationService.getAllStaffsByClub(clubId);
    }

    // create Account for Staff
    @PostMapping("/staff")
    public ResponseEntity registerStaff(@RequestBody StaffRegisterRequest staffRegisterRequest) {
        try {
            StaffResponse staff = authenticationService.registerStaff(staffRegisterRequest);
            return ResponseEntity.ok(staff);
        }catch(BadRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // block Staff by Club-Owner
    @PutMapping("/block-staff/{id}")
    public ResponseEntity<String> blockStaff(@PathVariable Long id) {
        boolean blockedCheck = authenticationService.blockStaff(id);
        if (blockedCheck)
            return ResponseEntity.ok("Blocked staff");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not found the staff to block !!");
    }
}
