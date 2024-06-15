package com.group3979.badmintonbookingbe.entity;

import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
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
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long promotionId;

    @Column(unique = true)
    private String promotionCode;

    private float discount; // specific price for discount
    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    PromotionStatus promotionStatus;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;
}