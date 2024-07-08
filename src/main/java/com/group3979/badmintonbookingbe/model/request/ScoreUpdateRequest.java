package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreUpdateRequest {
    private int firstPlayerSetScore;
    private int secondPlayerSetScore;
}
