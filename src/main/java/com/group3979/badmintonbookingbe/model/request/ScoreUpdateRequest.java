package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScoreUpdateRequest {
    private long scoreId;
    private int firstPlayerScore;
    private int secondPlayerScore;
}
