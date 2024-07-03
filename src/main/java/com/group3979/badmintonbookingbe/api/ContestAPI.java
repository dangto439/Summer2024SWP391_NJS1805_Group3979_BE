package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ContestRequest;
import com.group3979.badmintonbookingbe.model.response.ContestResponse;
import com.group3979.badmintonbookingbe.service.ContestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class ContestAPI {
    @Autowired
    ContestService contestService;
    @PostMapping("/contest")
    public ResponseEntity<ContestResponse> createContest(@RequestBody ContestRequest contestRequest){
        ContestResponse contestResponse = contestService.createContest(contestRequest);
        return ResponseEntity.ok(contestResponse);
    }
}
