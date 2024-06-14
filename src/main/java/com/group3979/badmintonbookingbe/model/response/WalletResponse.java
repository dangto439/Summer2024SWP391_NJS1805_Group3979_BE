package com.group3979.badmintonbookingbe.model.response;

import lombok.*;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.N;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    private Long walletId;
    private BigDecimal balance;
}
