package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private long bookingId;
    private double amount;
    private long receiverWalletId;
    private TransactionType type;
}
