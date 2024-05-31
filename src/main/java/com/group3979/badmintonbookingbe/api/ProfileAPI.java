package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.ProfileResponse;
import com.group3979.badmintonbookingbe.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
