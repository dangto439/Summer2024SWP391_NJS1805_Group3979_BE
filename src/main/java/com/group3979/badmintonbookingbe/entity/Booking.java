package com.group3979.badmintonbookingbe.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingId;

    private LocalDateTime bookingDate;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    private int amountTime;

    @Enumerated(EnumType.STRING)
    private ExpirationStatus expirationStatus;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "booking")
    List<Transaction> transactions;

    //
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Account account;

    @ManyToOne
    @JoinColumn(name = "club_id")
    Club club;

    @JsonIgnore
    @OneToMany(mappedBy = "booking")
    List<BookingDetail> bookingDetails;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    Promotion promotion;
}
