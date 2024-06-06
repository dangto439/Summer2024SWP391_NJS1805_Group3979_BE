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
public class ClubSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubSlotId;
    private float price;
    private int time;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;
}
