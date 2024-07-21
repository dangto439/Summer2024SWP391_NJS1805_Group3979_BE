package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ContestRequest;
import com.group3979.badmintonbookingbe.model.request.GameTimeRequest;
import com.group3979.badmintonbookingbe.model.request.UpdateContestRequest;
import com.group3979.badmintonbookingbe.model.response.ContestResponse;
import com.group3979.badmintonbookingbe.model.response.GameResponse;
import com.group3979.badmintonbookingbe.service.ContestService;
import com.group3979.badmintonbookingbe.service.GameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class ContestAPI {
    @Autowired
    ContestService contestService;
    @Autowired
    private GameService gameService;

    @PostMapping("/contest")
    public ResponseEntity<ContestResponse> createContest(@RequestBody ContestRequest contestRequest){
        ContestResponse contestResponse = contestService.createContest(contestRequest);
        return ResponseEntity.ok(contestResponse);
    }
    @GetMapping("/contests")
    public ResponseEntity<List<ContestResponse>> getAllContest(){
        List<ContestResponse> contestResponses = contestService.getAllContest();
        return ResponseEntity.ok(contestResponses);
    }

    @PutMapping("/contest")
    public ResponseEntity<ContestResponse> updateContest(@RequestBody UpdateContestRequest updateContestRequest){
        ContestResponse contestResponse = contestService.updateContest(updateContestRequest);
        return ResponseEntity.ok(contestResponse);
    }
    @GetMapping("/contest/{id}")
    public ResponseEntity<ContestResponse> getContestById(@PathVariable Long id){
        ContestResponse contestResponse = contestService.getContestById(id);
        return ResponseEntity.ok(contestResponse);
    }
    @GetMapping("/contest/current-account/{id}")
    public ResponseEntity<List<ContestResponse>> getContestsCurrentAccount(@PathVariable Long id){
//        List<ContestResponse> contestResponses = contestService.getContestsCurrentAccount();
        return ResponseEntity.ok(contestService.getContestsCurrentAccount(id));
    }
    @PutMapping("/contest/game/{gameId}")
    public ResponseEntity<GameResponse> updateGameTimeAndCourtSlot(@PathVariable long gameId,
                                                                   @RequestBody GameTimeRequest gameTimeRequest){
        GameResponse gameResponse = gameService.updateGameCourtSlotAndPlayingDate(gameId, gameTimeRequest);
        return ResponseEntity.ok(gameResponse);
    }

    @GetMapping("/contest/game/{gameId}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable long gameId){
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }
    @GetMapping("/contest/games/{contestId}")
    public ResponseEntity<List<GameResponse>> getGameByContestId(@PathVariable long contestId){
        return ResponseEntity.ok(gameService.getGamesByContestId(contestId));
    }
}
