package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.entity.Registration;
import com.group3979.badmintonbookingbe.model.request.RegistrationRequest;
import com.group3979.badmintonbookingbe.model.response.RegistrationResponse;
import com.group3979.badmintonbookingbe.service.RegistrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class RegistrationAPI {
    @Autowired
    RegistrationService registrationService;
    @PostMapping("/registration")
    public ResponseEntity registration(@RequestParam long contestId) {
        RegistrationResponse registrationResponse = registrationService.registrationContest(contestId);
        return ResponseEntity.ok(registrationResponse);
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<Registration>> transactions(@RequestParam long contestId) {
        List<Registration> registrationList = registrationService.getAllRegistrations(contestId);
        return ResponseEntity.ok(registrationList);
    }

    @PutMapping("/registration/{id}")
    public ResponseEntity updateRegistration(@PathVariable long id, @RequestBody RegistrationRequest registrationRequest) {
        Registration registrationUpdate = registrationService.updateRegistration(id, registrationRequest);
        return ResponseEntity.ok(registrationUpdate);
    }
}
