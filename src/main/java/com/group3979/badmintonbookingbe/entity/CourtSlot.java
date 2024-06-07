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
public class CourtSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courtSlotId;
    private float price;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;


}