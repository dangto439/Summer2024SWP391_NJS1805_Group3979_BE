package com.group3979.badmintonbookingbe.model.response;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    private long gameId;
    private long contestId;
    private LocalDate playingDate;
    private long firstPlayerId;
    private String firstPlayerName;
    private String secondPlayerName;
    private long secondPlayerId;
    private long courtSlotId;
    private long courtId;
    private String courtName;
    private int timeSlot;
    private int scoreFirstPlayer;
    private int scoreSecondPlayer;
    private int gameNumber;
    private int round;
}
