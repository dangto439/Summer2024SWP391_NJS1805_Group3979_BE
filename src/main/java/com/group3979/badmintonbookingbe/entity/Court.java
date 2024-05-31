package com.group3979.badmintonbookingbe.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long courtId;
    private String courtName;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;
}