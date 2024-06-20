package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.exception.InsufficientBalanceException;
import com.group3979.badmintonbookingbe.model.response.WalletResponse;
import com.group3979.badmintonbookingbe.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class WalletAPI {
    @Autowired
    WalletService walletService;

    @PostMapping("/vnpay")
    public ResponseEntity VNPAY(@RequestParam("amount") String amount) throws Exception {
        return ResponseEntity.ok(walletService.createUrl(amount));
    }

    @GetMapping("/wallet/{accountId}")
    public WalletResponse getWalletById(@PathVariable("accountId") Long accountId) throws NotFoundException {
        WalletResponse wallet = walletService.getWalletById(accountId);
        return ResponseEntity.ok(wallet).getBody();
    }

    @PostMapping("/wallet/{email}")
    public ResponseEntity addWallet(@PathVariable("email") String email) throws NotFoundException {
        WalletResponse wallet = walletService.createWalletByEmail(email);
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/wallet/{accountId}")
    public ResponseEntity updateWallet(@PathVariable("accountId") Long accountId, @RequestParam double newBalance) throws NotFoundException {
        WalletResponse wallet = walletService.updateWallet(accountId, newBalance);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/wallet/{accountId}/deposit")
    public ResponseEntity deposit(@PathVariable("accountId") Long accountId, @RequestParam double amount)
            throws NotFoundException {
        WalletResponse wallet = walletService.deposit(accountId, amount);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/wallet/{accountId}/withdrawal")
    public ResponseEntity withdrawal(@PathVariable("accountId") Long accountId, @RequestParam double amount)
            throws NotFoundException, InsufficientBalanceException {
        WalletResponse wallet = walletService.withdrawl(accountId, amount);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/wallet/transfer")
    public ResponseEntity transfer(@RequestParam Long senderAccountId, @RequestParam Long receiverAccountId,
                                   @RequestParam double amount) throws NotFoundException, InsufficientBalanceException {
        walletService.transfer(senderAccountId, receiverAccountId, amount);
        return ResponseEntity.ok("Chuyển tiền thành công");
    }
}
