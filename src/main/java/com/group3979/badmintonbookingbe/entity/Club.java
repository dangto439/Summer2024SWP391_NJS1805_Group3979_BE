package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clubId;
    private String clubName;
    private String clubAddress;
    private int openTime;
    private int closeTime;
    private String hotline;
    private ClubStatus clubStatus;
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    //
    @JsonIgnore
    @OneToMany(mappedBy = "club")
    List<Court> courts;

    // @JsonIgnore
    // @OneToMany(mappedBy = "club")
    // List<ImageClub> imageClubs;

}
