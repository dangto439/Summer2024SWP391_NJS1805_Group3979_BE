package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ChangePasswordRequest;
import com.group3979.badmintonbookingbe.model.request.ProfileRequest;
import com.group3979.badmintonbookingbe.model.response.ProfileResponse;
import com.group3979.badmintonbookingbe.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")

public class ProfileAPI {
    @Autowired
    ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {
        ProfileResponse account = profileService.getCurrentProfile();
        return ResponseEntity.ok(account);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody ProfileRequest profileRequest) {
        ProfileResponse account = profileService.updateProfile(profileRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/change-password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        try {
            ProfileResponse account = profileService.changePassword(changePasswordRequest);
            return ResponseEntity.ok(account);
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
