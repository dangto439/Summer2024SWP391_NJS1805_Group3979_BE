package com.group3979.badmintonbookingbe.entity;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingDetailId;
    private LocalDate playingDate;
    private double price;
    @Enumerated(EnumType.STRING)
    private BookingDetailStatus status;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    Booking booking;
    @Column(unique = true)
    private String checkInCode;
    @ManyToOne
    @JoinColumn(name = "court_slot_id")
    CourtSlot courtSlot;
}
