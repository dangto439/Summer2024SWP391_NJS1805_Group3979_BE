package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long slotId;
    private int time;

    @JsonIgnore
    @OneToMany(mappedBy = "slot")
    List<CourtSlot> courtSlots;
}
