package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.EmailDetail;
import com.group3979.badmintonbookingbe.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.LoginRequest;
import com.group3979.badmintonbookingbe.model.RegisterRequest;
import com.group3979.badmintonbookingbe.service.AuthenticationService;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
public class AuthenticationAPI {

    // nhận request từ front-end
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EmailService emailService;

    @PostMapping("register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
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
    public void sendMail() {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient("dangto0898494156@gmail.com");
        emailDetail.setSubject("Welcome");
        emailDetail.setMsgBody("Welcome to my website");
        emailService.sendMailTemplate(emailDetail);
    }

}
