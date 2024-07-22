package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.ContestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long contestId;

    private double participationPrice;

    private String name;

    private int capacity;

    private double firstPrize;

    private double secondPrize;

    private String urlBanner;

    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private ContestStatus contestStatus;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @JsonIgnore
    @OneToMany(mappedBy = "contest")
    List<Registration> registrations;

    @JsonIgnore
    @OneToMany(mappedBy = "contest")
    List<Game> games;

    @JsonIgnore
    @OneToMany(mappedBy = "contest")
    List<Transaction> transactions;
}
