package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScoreRequest {
    private long gameId;
    private int firstPlayerSetScore;
    private int secondPlayerSetScore;
}
