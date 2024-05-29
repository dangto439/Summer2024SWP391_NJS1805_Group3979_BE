package com.group3979.badmintonbookingbe.entity;


<<<<<<< Updated upstream
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Courst {
    private int Courstid;
=======
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long CourtId;
    private String CourtName;

    @ManyToOne
    @JoinColumn (name = "club_id")
    private Club club;
>>>>>>> Stashed changes
}
