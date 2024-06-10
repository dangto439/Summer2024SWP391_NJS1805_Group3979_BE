package com.group3979.badmintonbookingbe.entity;


import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingId;

    private Date bookingDate;

    private float totalPrice;

    @Enumerated(EnumType.STRING)

    private BookingType bookingType;

    private int amountTime;

    @Enumerated(EnumType.STRING)
    private ExpirationStatus expirationStatus;

    @ManyToOne
    @JoinColumn(name = "acount_id")
    Account account;

    @ManyToOne
    @JoinColumn(name = "club_id")
    Club club;
}
