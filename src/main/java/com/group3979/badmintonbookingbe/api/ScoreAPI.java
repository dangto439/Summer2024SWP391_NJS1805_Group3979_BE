package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ScoreRequest;
import com.group3979.badmintonbookingbe.model.response.ScoreResponse;
import com.group3979.badmintonbookingbe.service.ScoreService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class ScoreAPI {
    @Autowired
    ScoreService scoreService;

    @GetMapping("/score/{gameId}")
    public ResponseEntity<List<ScoreResponse>> getScores(@PathVariable long gameId) {
        List<ScoreResponse> scores = scoreService.getScoreByGameId(gameId);
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/score")
    public ResponseEntity<ScoreResponse> addScore(@RequestBody ScoreRequest scoreRequest) {
        ScoreResponse scoreResponse = scoreService.createScore(scoreRequest);
        return ResponseEntity.ok(scoreResponse);
    }

    @PutMapping("/score/{scoreId}")
    public ResponseEntity<ScoreResponse> updateScore(
            @PathVariable("scoreId") long scoreId,
            @RequestParam("firstPlayerScore") int firstPlayerScore,
            @RequestParam("secondPlayerScore") int secondPlayerScore) {

        ScoreResponse updatedScore = scoreService.updateScore(scoreId, firstPlayerScore, secondPlayerScore);
        return ResponseEntity.ok(updatedScore);
    }
}
