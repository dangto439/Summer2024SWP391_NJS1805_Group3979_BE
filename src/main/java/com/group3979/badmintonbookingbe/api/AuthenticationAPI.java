package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.*;
import com.group3979.badmintonbookingbe.model.response.AccountResponse;
import com.group3979.badmintonbookingbe.model.response.RevenueResponse;
import com.group3979.badmintonbookingbe.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.service.AuthenticationService;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

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
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) throws SQLIntegrityConstraintViolationException {
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        emailService.sendPasswordResetMail(resetPasswordRequest);
        return ResponseEntity.ok("Email đặt lại mật khẩu đã được gửi thành công!");
    }

    // reset password
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        authenticationService.resetPassword(newPasswordRequest);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }

    @PostMapping("/login-google")
    public ResponseEntity<AccountResponse> loginGoogle(@RequestBody LoginGoogleRequest logingoogleRequest) {
        return ResponseEntity.ok(authenticationService.loginGoogle(logingoogleRequest));
    }

    //Admin
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity testAdmin() {
        return ResponseEntity.ok("Admin!");
    }

    @GetMapping("/get-all-account")
    public ResponseEntity getAllAccount() {
        return ResponseEntity.ok(authenticationService.getAllAccounts());
    }

    @PutMapping("/block/{email}")
    public ResponseEntity blockUser(@PathVariable String email) {
        return ResponseEntity.ok(authenticationService.blockUser(email));
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable long id) {
        Account account = authenticationService.getAccountById(id);
        return account;
    }

    @PutMapping("/update-account-admin")
    public Account updateAccountAdmin(@RequestBody UpdateAccountRequest updateAccountRequest) {
        Account account = authenticationService.updateAccount(updateAccountRequest);
        return account;
    }

    @GetMapping("/dashboard-admin-tiny-chart/{year}")
    public List<RevenueResponse> getRevenueResponse(@PathVariable int year) {
        return authenticationService.getRevenueResponse(year);
    }
}
