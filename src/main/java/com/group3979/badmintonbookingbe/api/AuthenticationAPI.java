package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.*;
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

import java.util.HashMap;
import java.util.Map;

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
//        emailService.sendMail(account.getEmail(), account.getName());

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





   @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ResetPasswordRequest resetpasswordrequest) {
        emailService.sendPasswordResetMail(resetpasswordrequest);
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    //Xác nhận token và hiển thị trang đặt lại mật khẩu
    @GetMapping("/reset-password")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token is valid, show reset password page");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    //Endpoint POST để cập nhật mật khẩu mới
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        boolean isValid = tokenService.validateToken(newPasswordRequest.getToken());
        if (isValid) {
            Account account = tokenService.getAccountFromToken(newPasswordRequest.getToken());
            authenticationService.updatePassword(account, newPasswordRequest.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
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
