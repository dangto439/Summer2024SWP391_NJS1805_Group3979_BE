package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.eNum.TransferContestRequest;
import com.group3979.badmintonbookingbe.exception.InsufficientBalanceException;
import com.group3979.badmintonbookingbe.model.request.TransferRequest;
import com.group3979.badmintonbookingbe.model.response.WalletResponse;
import com.group3979.badmintonbookingbe.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/wallet-email/{email}")
    public WalletResponse getWalletByEmail(@PathVariable("email") String email) throws NotFoundException {
        WalletResponse wallet = walletService.getWalletByEmail(email);
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

    @PostMapping("/wallet/{walletId}/deposit")
    public ResponseEntity deposit(@PathVariable("walletId") Long walletId, @RequestParam double amount)
            throws NotFoundException {
        WalletResponse wallet = walletService.deposit(walletId, amount);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/wallet/transfer")
    public ResponseEntity transfer(@RequestBody TransferRequest transferRequest) throws NotFoundException, InsufficientBalanceException {
        walletService.transfer(transferRequest);
        return ResponseEntity.ok("Chuyển tiền thành công");
    }

    @PostMapping("/wallet/transfer-booking")
    public ResponseEntity transferBooking(@RequestBody TransferRequest transferRequest) throws NotFoundException {
        walletService.transferOnBooking(transferRequest);
        return ResponseEntity.ok("Chuyển tiền thành công");
    }

    @GetMapping("/wallet-owner/{clubId}")
    public ResponseEntity ownerWallet(@PathVariable Long clubId) throws NotFoundException {
        WalletResponse wallet = walletService.getWalletOfClubOwner(clubId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/wallet/transfer-contest")
    public ResponseEntity transferContest(@RequestBody TransferContestRequest transferContestRequest) throws NotFoundException {
        walletService.transferOnContest(transferContestRequest);
        return ResponseEntity.ok("Chuyển tiền thành công");
    }
}
