package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class WalletApi {

    @Autowired
    WalletService walletService;

    @PostMapping("/vnpay")
    public ResponseEntity VNPAY(@RequestParam("amount") String amount) throws Exception {
        return ResponseEntity.ok(walletService.createUrl(amount));
    }
}
