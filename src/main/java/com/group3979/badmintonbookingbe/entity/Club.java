package com.group3979.badmintonbookingbe.entity;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import jakarta.persistence.*;

import java.sql.Time;

@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clubId;
    private String clubName;
    private String clubAddress;
    private Time openTime;
    private Time closeTime;
    private String hotline;
    private ClubStatus clubStatus;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;
//    private String clubDistrict;
//    private String clubCity;


}
