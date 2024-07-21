package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Contest;
import com.group3979.badmintonbookingbe.entity.Game;
import com.group3979.badmintonbookingbe.entity.Registration;
import com.group3979.badmintonbookingbe.model.request.GameTimeRequest;
import com.group3979.badmintonbookingbe.model.response.GameResponse;
import com.group3979.badmintonbookingbe.repository.ICourtSlotRepository;
import com.group3979.badmintonbookingbe.repository.IGameRepository;
import com.group3979.badmintonbookingbe.repository.IRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GameService {
    @Autowired
    IGameRepository gameRepository;
    @Autowired
    IRegistrationRepository registrationRepository;
    @Autowired
    ICourtSlotRepository courtSlotRepository;

    public void createMatchesContest(int capacity, Contest contest) {
        int gameNumber = 1;
        int currentRound = 1;
        int gamesInCurrentRound = capacity / 2;

        while (gameNumber < capacity) {
            for (int i = 0; i < gamesInCurrentRound; i++) {
                Game game = Game.builder()
                        .gameNumber(gameNumber)
                        .contest(contest)
                        .round(currentRound)
                        .build();
                gameRepository.save(game);
                gameNumber++;
            }
            currentRound++;
            gamesInCurrentRound /= 2;
        }
    }

    public void arrangePlayersContest(Contest contest) {
        List<Game> games = gameRepository.findGamesByContest(contest);
        List<Registration> registrations = registrationRepository.findRegistrationsByContest(contest);
        Collections.shuffle(registrations);
        games.removeIf(game -> game.getGameNumber() > (contest.getCapacity() / 2));
        int i = 0;
        for (Game game : games) {
            game.setFirstPlayer(registrations.get(i).getAccount());
            i++;
            game.setSecondPlayer(registrations.get(i).getAccount());
            i++;
            gameRepository.save(game);
        }
    }

    public void updateScore(int scorePlayer1, int scorePlayer2, Game game) {
        game.setScoreFirstPlayer(scorePlayer1);
        game.setScoreSecondPlayer(scorePlayer2);
        gameRepository.save(game);
        Account winnerPlayer ;
        if(scorePlayer1 > scorePlayer2){
            winnerPlayer = game.getFirstPlayer();
        }else {
            winnerPlayer = game.getSecondPlayer();
        }
        updatePlayerNextRound(game, winnerPlayer);
    }


    public void updatePlayerNextRound(Game game, Account winnerPlayer) {
        List<Game> games = gameRepository.findGamesByContest(game.getContest());
        games.sort(Comparator.comparingInt(Game::getGameNumber));

        int currentRound = game.getRound();
        int nextRound = currentRound + 1;
        int nextGameNumber = getNextGameNumber(game.getGameNumber(), currentRound, game.getContest().getCapacity());

        Game nextGame = games.stream()
                .filter(g -> g.getRound() == nextRound)
                .filter(g -> g.getGameNumber() == nextGameNumber)
                .findFirst()
                .orElse(null);

        if (nextGame != null) {
            if (game.getGameNumber() % 2 != 0) {
                nextGame.setFirstPlayer(winnerPlayer);
            } else {
                nextGame.setSecondPlayer(winnerPlayer);
            }
            gameRepository.save(nextGame);
        }
    }

    private int getNextGameNumber(int gameNumber, int currentRound, int capacity) {
        int gamesInPreviousRounds = 0;
        for (int round = 1; round < currentRound; round++) {
            gamesInPreviousRounds += capacity / (1 << round);
        }
        int gamesInThisRound = capacity / (1 << currentRound);
        return gamesInPreviousRounds + gamesInThisRound + (gameNumber - gamesInPreviousRounds + 1) / 2;
    }
    public GameResponse getGameById(long id){
        return this.buildGameResponse(gameRepository.findGameByGameId(id));
    }
    public GameResponse updateGameCourtSlotAndPlayingDate(long id, GameTimeRequest gameTimeRequest){
    Game game = gameRepository.findGameByGameId(id);
    game.setPlayingDate(gameTimeRequest.getPlayingDate());
    game.setCourtSlot(courtSlotRepository.findCourtSlotByCourtSlotId(gameTimeRequest.getCourtSlotId()));
    game = gameRepository.save(game);
    return this.buildGameResponse(game);
    }
    public GameResponse buildGameResponse(Game game){
        long firstPlayerId = 0;
        long secondPlayerId = 0;
        String name1 = null;
        String name2 = null;
        long courtSlotId = 0 ;
        String courtName = null;
        long courtId = 0;
        int timeSlot = 0;
        if(game.getFirstPlayer() != null ){
            firstPlayerId = game.getFirstPlayer().getId();
            name1 = game.getFirstPlayer().getName();
        }
        if(game.getSecondPlayer() != null ){
            secondPlayerId = game.getSecondPlayer().getId();
            name2 = game.getSecondPlayer().getName();
        }
        if(game.getCourtSlot() != null){
            courtSlotId = game.getCourtSlot().getCourtSlotId() ;
            courtName = game.getCourtSlot().getCourt().getCourtName();
            courtId =game.getCourtSlot().getCourt().getCourtId();
            timeSlot = game.getCourtSlot().getSlot().getTime();
        }
        return GameResponse.builder()
                .playingDate(game.getPlayingDate())
                .firstPlayerName(name1)
                .timeSlot(timeSlot)
                .courtId(courtId)
                .courtName(courtName)
                .courtSlotId(courtSlotId)
                .secondPlayerName(name2)
                .contestId(game.getContest().getContestId())
                .firstPlayerId(firstPlayerId)
                .secondPlayerId(secondPlayerId)
                .gameId(game.getGameId())
                .round(game.getRound())
                .scoreFirstPlayer(game.getScoreFirstPlayer())
                .scoreSecondPlayer(game.getScoreSecondPlayer())
                .gameNumber(game.getGameNumber())
                .build();
    }
    public List<GameResponse> getGamesByContestId(long contestId){
        List<Game> games = gameRepository.findGamesByContest_ContestId(contestId);
        List<GameResponse> gameResponses = new ArrayList<>();
        for(Game game: games){
            gameResponses.add(this.buildGameResponse(game));
        }
        return gameResponses;
    }

}
