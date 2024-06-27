package com.group3979.badmintonbookingbe.eNum;

import lombok.Getter;

@Getter
public enum TransactionType {
    REFUND("Giao dịch hoàn tiền"),
    DEPOSIT("Giao dịch nạp tiền"),
    TRANSFER("Giao dịch chuyển tiền"),
    PENDING("Giao dịch đang chờ xử lý"),
    ;

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
}
