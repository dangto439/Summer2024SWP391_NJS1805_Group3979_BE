package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.SetNumber;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse {
    private long scoreId;
    private long gameId;
    private SetNumber setNumber;
    private int firstPlayerSetScore;
    private int secondPlayerSetScore;
}
