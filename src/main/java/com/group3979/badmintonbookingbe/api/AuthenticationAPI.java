package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.LoginGoogleRequest;
import com.group3979.badmintonbookingbe.model.request.LoginRequest;
import com.group3979.badmintonbookingbe.model.request.NewPasswordRequest;
import com.group3979.badmintonbookingbe.model.request.RegisterRequest;
import com.group3979.badmintonbookingbe.model.request.ResetPasswordRequest;
import com.group3979.badmintonbookingbe.model.response.AccountReponse;
import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import com.group3979.badmintonbookingbe.service.EmailService;
import com.group3979.badmintonbookingbe.service.TokenService;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.service.AuthenticationService;
import java.util.List;

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
    public ResponseEntity<AccountReponse> loginGoogle(@RequestBody LoginGoogleRequest logingoogleRequest) {
        return ResponseEntity.ok(authenticationService.loginGoogle(logingoogleRequest));
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity testAdmin() {
        return ResponseEntity.ok("Admin!!!!!!!!!!!!");
    }

    // getStaffLists of Club-Owner
    @GetMapping("/staff")
    public List<Account> getStaffs() {
        return authenticationService.getAllStaffs();
    }

    @PostMapping("/staff")
    public ResponseEntity registerStaff(@RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse staff = authenticationService.registerStaff(registerRequest);
        return ResponseEntity.ok(staff);
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
