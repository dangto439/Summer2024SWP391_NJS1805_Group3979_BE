package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clubId;
    private String clubName;
    private String clubAddress;
    private String district;
    private String province;
    private int openTime;
    private int closeTime;
    private String hotline;
    @Enumerated(EnumType.STRING)
    private ClubStatus clubStatus;
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    @OneToOne(mappedBy = "club")
    private DiscountRule discountRule;

    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<Court> courts;

    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<ImageClub> imageClubs;

    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<Account> staff;

    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<Booking> bookings;

    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<Promotion> promotions;
}
