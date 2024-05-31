package com.group3979.badmintonbookingbe.api;

import com.google.firebase.auth.FirebaseAuthException;
import com.group3979.badmintonbookingbe.model.*;
import com.group3979.badmintonbookingbe.service.EmailService;
import com.group3979.badmintonbookingbe.service.TokenService;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.service.AuthenticationService;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class AuthenticationAPI {
@Autowired
    AccountUtils accountUtils;
    // nhận request từ front-end
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    TokenService tokenService;

    @PostMapping("register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/test")
    public Account test() {
        return accountUtils.getCurrentAccount();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }
    @PostMapping("/login-google")
    public ResponseEntity loginGoogle(@RequestBody LoginGoogleRequest loginGoogleRequest) throws FirebaseAuthException {
        AccountReponse accountReponse = authenticationService.loginGoogle(loginGoogleRequest);
        return ResponseEntity.ok(accountReponse);
    }
    //send email forgot password
   @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        emailService.sendPasswordResetMail(resetPasswordRequest);
        return ResponseEntity.ok("Password reset email sent successfully");
    }
    //reset password
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        authenticationService.resetPassword(newPasswordRequest);
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @GetMapping("/test2")
    public ResponseEntity test1() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity testAdmin() {
        return ResponseEntity.ok("Admin!!!!!!!!!!!!");
    }


}
