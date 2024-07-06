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

    // CREATE - tao ra Score cho 1 tran dau
    public List<ScoreResponse> createScore(List<ScoreRequest> scoreRequests) {
        List<ScoreResponse> scores = new ArrayList<>();
        // find Game
        int temp = 1; // temp
        int finalFirstPlayer = 0;
        int finalSecondPlayer = 0;
        for(ScoreRequest scoreRequest : scoreRequests) {
            Game game = gameRepository.findById(scoreRequest.getGameId()).get();
            if (game == null) {
                throw new CustomException("Không tìm thấy trận đấu với ID: " + scoreRequest.getGameId());
            }
            // Create Score
            Score score = new Score();
            score.setGame(game);

            if(temp == 1){
                score.setSetNumber(SetNumber.FIRSTSET);
            }else if(temp == 2){
                score.setSetNumber(SetNumber.SECONDSET);
            }else{
                score.setSetNumber(SetNumber.THIRDSET);
            }
            temp++;

            score.setFirstPlayerSetScore(scoreRequest.getFirstPlayerSetScore());
            score.setSecondPlayerSetScore(scoreRequest.getSecondPlayerSetScore());


            if(scoreRequest.getFirstPlayerSetScore() > scoreRequest.getSecondPlayerSetScore()){
                finalFirstPlayer += 1;
            }else {
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

    // UPDATE - update khi nao ghi diem(create) sai thoi
    public List<ScoreResponse> updateScore(List<ScoreUpdateRequest> scoreUpdateRequestList) {
        List<ScoreResponse> scoreResponseList = new ArrayList<>();
        for(ScoreUpdateRequest scoreUpdateRequest : scoreUpdateRequestList) {
            Score score = scoreRepository.findById(scoreUpdateRequest.getScoreId()).orElseThrow(() ->
                    new RuntimeException("Không tìm thấy điểm số với id " + scoreUpdateRequest.getScoreId()));

            score.setFirstPlayerSetScore(scoreUpdateRequest.getFirstPlayerScore());
            score.setSecondPlayerSetScore(scoreUpdateRequest.getSecondPlayerScore());

            score = scoreRepository.save(score);
            scoreResponseList.add(buildScoreResponse(score));
        }
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
