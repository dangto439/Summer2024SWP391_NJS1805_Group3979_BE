package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.TransactionType;
import com.group3979.badmintonbookingbe.entity.Wallet;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private long transactionId;
    private String description;
    private double amount;
    private String timestamp;
    private TransactionType type;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private long bookingId;

}
