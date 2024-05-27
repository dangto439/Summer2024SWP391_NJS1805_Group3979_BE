package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.EmailDetail;
import com.group3979.badmintonbookingbe.model.ResetPasswordRequest;
import com.group3979.badmintonbookingbe.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.LoginRequest;
import com.group3979.badmintonbookingbe.model.RegisterRequest;
import com.group3979.badmintonbookingbe.service.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class AuthenticationAPI {

    // nhận request từ front-end
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EmailService emailService;

    @PostMapping("register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.register(registerRequest);
        emailService.sendMail(account.getEmail(), account.getName());

        return ResponseEntity.ok(account);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    //@PutMapping("/reset-password")



   @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ResetPasswordRequest resetpasswordrequest) {
        emailService.sendPasswordResetMail(resetpasswordrequest.getEmail());
        return ResponseEntity.ok("Password reset email sent successfully");
    }


    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity testAdmin() {
        return ResponseEntity.ok("Admin!!!!!!!!!!!!");
    }

    @GetMapping("/send-email")
    public void sendMail(String email, String name) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(email);
        emailDetail.setSubject("Welcome");
        emailDetail.setMsgBody("Welcome to my website");
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        emailService.sendMailTemplate(emailDetail, variables, "emailtemplate");
    }


}
