package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.eNum.CourtStatus;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long courtId;
    private String courtName;
    @Enumerated(EnumType.STRING)
    private CourtStatus courtStatus;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @JsonIgnore
    @OneToMany(mappedBy = "court")
    List<CourtSlot> courtSlots;
}