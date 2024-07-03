package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Game;
import com.group3979.badmintonbookingbe.entity.Score;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.ScoreRequest;
import com.group3979.badmintonbookingbe.model.response.ScoreResponse;
import com.group3979.badmintonbookingbe.repository.IGameRepository;
import com.group3979.badmintonbookingbe.repository.IScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreService {
    @Autowired
    IScoreRepository scoreRepository;
    @Autowired
    IGameRepository gameRepository;

    // CREATE - tao ra Score cho 1 tran dau
    public ScoreResponse createScore(ScoreRequest scoreRequest) {
        // find Game
        Game game = gameRepository.findById(scoreRequest.getGameId()).get();
        if (game == null) {
            throw new CustomException("Không tìm thấy trận đấu với ID: " + scoreRequest.getGameId());
        }
        // Create Score
        Score score = new Score();
        score.setGame(game);
        score.setSetNumber(scoreRequest.getSetNumber());
        score.setFirstPlayerSetScore(0);
        score.setSecondPlayerSetScore(0);

        score = scoreRepository.save(score);
        return buildScoreResponse(score);
    }

    // READ - get diem so cua tran dau
    public List<ScoreResponse> getScoreByGameId(long gameId) {
        Game game = gameRepository.findById(gameId).get();
        if (game == null) {
            throw new CustomException("Không tìm thấy trận đấu với ID: " + gameId);
        }
        List<Score> scoreList = scoreRepository.findScoresByGame(game);
        List<ScoreResponse> scoreResponseList = new ArrayList<>();
        for (Score score : scoreList) {
            scoreResponseList.add(buildScoreResponse(score));
        }
        return scoreResponseList;
    }

    // UPDATE - cap nhat diem so cua 1 tran dau
    public ScoreResponse updateScore(long scoreId, int firstPlayerScore, int secondPlayerScore) {
        Score score = scoreRepository.findById(scoreId).orElseThrow(() ->
                new RuntimeException("Không tìm thấy điểm số với id " + scoreId));

        score.setFirstPlayerSetScore(firstPlayerScore);
        score.setSecondPlayerSetScore(secondPlayerScore);

        score = scoreRepository.save(score);
        return buildScoreResponse(score);
    }

    public ScoreResponse buildScoreResponse(Score score) {
        return ScoreResponse.builder().
                scoreId(score.getScoreId())
                .gameId(score.getGame().getGameId())
                .setNumber(score.getSetNumber())
                .firstPlayerSetScore(score.getFirstPlayerSetScore())
                .secondPlayerSetScore(score.getSecondPlayerSetScore())
                .build();
    }
}
