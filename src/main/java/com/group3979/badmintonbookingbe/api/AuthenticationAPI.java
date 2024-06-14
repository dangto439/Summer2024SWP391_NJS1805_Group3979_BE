package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.*;
import com.group3979.badmintonbookingbe.model.response.AccountResponse;
import com.group3979.badmintonbookingbe.service.EmailService;
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
    // nhận request từ front-end
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private EmailService emailService;



    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        emailService.sendPasswordResetMail(resetPasswordRequest);
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    // reset password
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        authenticationService.resetPassword(newPasswordRequest);
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PostMapping("/login-google")
    public ResponseEntity<AccountResponse> loginGoogle(@RequestBody LoginGoogleRequest logingoogleRequest) {
        return ResponseEntity.ok(authenticationService.loginGoogle(logingoogleRequest));
    }

    //Admin
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity testAdmin() {
        return ResponseEntity.ok("Admin!!!!!!!!!!!!");
    }

    @GetMapping("/get-all-account")
    public ResponseEntity getAllAccount() {
        return ResponseEntity.ok(authenticationService.getAllAccounts());
    }

    @PutMapping("/block/{email}")
    public ResponseEntity blockUser(@PathVariable String email) {
        return ResponseEntity.ok(authenticationService.blockUser(email));
    }
}
