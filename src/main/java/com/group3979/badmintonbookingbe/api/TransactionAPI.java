package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.eNum.TransactionType;
import com.group3979.badmintonbookingbe.model.request.TransactionRequest;
import com.group3979.badmintonbookingbe.model.response.TransactionResponse;
import com.group3979.badmintonbookingbe.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class TransactionAPI {
    @Autowired
    TransactionService transactionService;

    @GetMapping("/get-transactions")
    public ResponseEntity getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/get-transactions/{accountId}")
    public ResponseEntity getTransactionById(@PathVariable("accountId") Long accountId) throws NotFoundException {
        return ResponseEntity.ok(transactionService.getTransactionsForAccount(accountId));
    }

    @PostMapping("/transaction")
    public ResponseEntity createTransaction(@RequestBody TransactionRequest transactionRequest) {
        TransactionResponse transaction = transactionService.createTransaction(transactionRequest);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/transaction/{transactionId}")
    public ResponseEntity updateTransaction(@PathVariable("transactionId") Long transactionId,
                                            @RequestParam TransactionType transactionType,@RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.updateTransactionType(transactionRequest, transactionId, transactionType));
    }

    @PostMapping("/transactionV2")
    public ResponseEntity createTransactionV2(@RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.createTransactionV2(transactionRequest));
    }
}
