package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.SetNumber;
import com.group3979.badmintonbookingbe.entity.Game;
import com.group3979.badmintonbookingbe.entity.Score;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.ScoreRequest;
import com.group3979.badmintonbookingbe.model.request.ScoreUpdateRequest;
import com.group3979.badmintonbookingbe.model.response.ScoreResponse;
import com.group3979.badmintonbookingbe.repository.IGameRepository;
import com.group3979.badmintonbookingbe.repository.IScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreService {
    @Autowired
    IScoreRepository scoreRepository;
    @Autowired
    IGameRepository gameRepository;
    @Autowired
    GameService gameService;

    @Transactional
    // CREATE - tao ra Score cho 1 tran dau
    public List<ScoreResponse> createScore(List<ScoreRequest> scoreRequests) {
        List<ScoreResponse> scores = new ArrayList<>();
        // find Game
        int temp = 1; // temp
        int finalFirstPlayer = 0;
        int finalSecondPlayer = 0;
        for (ScoreRequest scoreRequest : scoreRequests) {
            Game game = gameRepository.findById(scoreRequest.getGameId()).get();
            if (game == null) {
                throw new CustomException("Không tìm thấy trận đấu với ID: " + scoreRequest.getGameId());
            }
            // Create Score
            Score score = new Score();
            score.setGame(game);

            if (temp == 1) {
                score.setSetNumber(SetNumber.FIRSTSET);
            } else if (temp == 2) {
                score.setSetNumber(SetNumber.SECONDSET);
            } else {
                score.setSetNumber(SetNumber.THIRDSET);
            }
            temp++;

            score.setFirstPlayerSetScore(scoreRequest.getFirstPlayerSetScore());
            score.setSecondPlayerSetScore(scoreRequest.getSecondPlayerSetScore());


            if (scoreRequest.getFirstPlayerSetScore() > scoreRequest.getSecondPlayerSetScore()) {
                finalFirstPlayer += 1;
            } else {
                finalSecondPlayer += 1;
            }

            score = scoreRepository.save(score);
            scores.add(buildScoreResponse(score));

        }
        gameService.updateScore(finalFirstPlayer, finalSecondPlayer,
                gameRepository.findGameByGameId(scoreRequests.get(0).getGameId()));
        return scores;
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

    @Transactional
    // UPDATE - update khi nao ghi diem(create) sai thoi
    public List<ScoreResponse> updateScore(long gameId, List<ScoreUpdateRequest> scoreUpdateRequests) {
        // find Game
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new RuntimeException("Không tìm thấy trận đấu với id " + gameId);
        }

        List<Score> scoreList = scoreRepository.findScoresByGame(game);
        if (scoreList.size() == 3 && scoreUpdateRequests.size() == 2) {
            scoreRepository.removeBySetNumberAndGame(SetNumber.THIRDSET, game);
            // after remove, update scoreList
            scoreList = scoreRepository.findScoresByGame(game);
        }else if(scoreList.size() == 2 && scoreUpdateRequests.size() == 3) {
            Score thirdScore = new Score();
            thirdScore.setSetNumber(SetNumber.THIRDSET);
            thirdScore.setGame(game);
            scoreRepository.save(thirdScore);
            scoreList = scoreRepository.findScoresByGame(game);
        }

        List<ScoreResponse> scoreResponseList = new ArrayList<>();
        int finalFirstPlayer = 0;
        int finalSecondPlayer = 0;

        for (int i = 0; i < scoreUpdateRequests.size(); i++) {
            ScoreUpdateRequest scoreUpdateRequest = scoreUpdateRequests.get(i);

            Score score = scoreList.get(i);

            score.setFirstPlayerSetScore(scoreUpdateRequest.getFirstPlayerSetScore());
            score.setSecondPlayerSetScore(scoreUpdateRequest.getSecondPlayerSetScore());

            if (scoreUpdateRequest.getFirstPlayerSetScore() > scoreUpdateRequest.getSecondPlayerSetScore()) {
                finalFirstPlayer += 1;
            } else {
                finalSecondPlayer += 1;
            }

            score = scoreRepository.save(score);
            scoreResponseList.add(buildScoreResponse(score));
        }

        gameService.updateScore(finalFirstPlayer, finalSecondPlayer, game);
        return scoreResponseList;
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
