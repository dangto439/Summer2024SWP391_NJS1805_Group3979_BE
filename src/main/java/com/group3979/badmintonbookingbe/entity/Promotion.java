package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long promotionId;

    @Column(unique = true)
    private String promotionCode;

    private double discount; // specific price for discount
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    PromotionStatus promotionStatus;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @JsonIgnore
    @OneToMany(mappedBy = "promotion")
    List<Booking> bookings;
}