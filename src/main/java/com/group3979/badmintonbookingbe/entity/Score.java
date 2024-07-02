package com.group3979.badmintonbookingbe.entity;

import com.group3979.badmintonbookingbe.eNum.SetNumber;
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
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long scoreId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    private SetNumber setNumber;

    private int firstPlayerSetScore;

    private int secondPlayerSetScore;
}
