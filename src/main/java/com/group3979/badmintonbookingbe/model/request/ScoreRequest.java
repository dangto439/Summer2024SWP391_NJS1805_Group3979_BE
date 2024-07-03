package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.SetNumber;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScoreRequest {
    private long gameId;
    private SetNumber setNumber;
    private int firstPlayerSetScore;
    private int secondPlayerSetScore;
}
