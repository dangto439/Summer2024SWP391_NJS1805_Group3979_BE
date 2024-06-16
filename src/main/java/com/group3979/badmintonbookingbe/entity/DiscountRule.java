package com.group3979.badmintonbookingbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountRuleId;
    private double flexiblePercent;
    private double fixedPercent;

    @OneToOne
    @JoinColumn(name = "club_id")
    private Club club;
}
