package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferContestRequest {
    private long senderWalletId;
    private long receiverWalletId;
    private double amount;
    private long contestId;
}
