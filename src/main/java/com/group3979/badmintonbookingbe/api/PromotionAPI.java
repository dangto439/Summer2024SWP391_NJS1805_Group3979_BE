package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.PromotionRequest;
import com.group3979.badmintonbookingbe.model.response.PromotionResponse;
import com.group3979.badmintonbookingbe.service.PromotionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class PromotionAPI {
    @Autowired
    PromotionService promotionService;

    @PostMapping("/promotion/{clubId}")
    public ResponseEntity createPromotion(@PathVariable Long clubId, @RequestBody PromotionRequest promotionRequest) {
        PromotionResponse promotion = promotionService.createPromotion(clubId, promotionRequest);
        return  ResponseEntity.ok(promotion);
    }

    @GetMapping("/promotion/{clubId}")
    public ResponseEntity getPromotion(@PathVariable Long clubId) {
        List<PromotionResponse> promotionResponseList = promotionService.getAllPromotions(clubId);
        return  ResponseEntity.ok(promotionResponseList);
    }

    @PutMapping("/promotion/{promotionId}")
    public ResponseEntity updatePromotion(@PathVariable Long promotionId, @RequestBody PromotionRequest promotionRequest) {
        PromotionResponse promotion = promotionService.updatePromotion(promotionId, promotionRequest);
        return  ResponseEntity.ok(promotion);
    }
}
