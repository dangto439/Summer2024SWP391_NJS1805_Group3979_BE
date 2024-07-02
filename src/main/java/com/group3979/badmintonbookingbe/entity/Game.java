package com.group3979.badmintonbookingbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    private LocalDate playingDate;

    @ManyToOne
    @JoinColumn(name = "first_player_id")
    private Account firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id")
    private Account secondPlayer;

    private int scoreFirstPlayer;

    private int scoreSecondPlayer;

    private int gameNumber;

    @ManyToOne
    @JoinColumn(name = "court_slot_id")
    private CourtSlot courtSlot;

    @JsonIgnore
    @OneToMany(mappedBy = "game")
    List<Score> scores;

}
