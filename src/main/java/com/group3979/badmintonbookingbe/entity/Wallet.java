package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;
    private double balance;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver_wallet")
    List<Transaction> transactions;

    @JsonIgnore
    @OneToMany(mappedBy = "sender_wallet")
    List<Transaction> transactionsV2;
}
