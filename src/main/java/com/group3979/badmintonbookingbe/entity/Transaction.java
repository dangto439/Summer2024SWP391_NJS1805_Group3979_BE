package com.group3979.badmintonbookingbe.entity;

import com.group3979.badmintonbookingbe.eNum.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private String description;
    private double amount;
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet sender_wallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiver_wallet;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
