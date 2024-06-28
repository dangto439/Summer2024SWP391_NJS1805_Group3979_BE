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
public class CourtSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courtSlotId;
    private double price;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @JsonIgnore
    @OneToMany(mappedBy = "courtSlot")
    List<BookingDetail> bookingDetails;
}