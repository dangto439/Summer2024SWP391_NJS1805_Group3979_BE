package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.PromotionRequest;
import com.group3979.badmintonbookingbe.model.response.PromotionResponse;
import com.group3979.badmintonbookingbe.service.PromotionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javassist.NotFoundException;
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
    public ResponseEntity<PromotionResponse> createPromotion(@PathVariable Long clubId, @RequestBody PromotionRequest promotionRequest) {
        PromotionResponse promotion = promotionService.createPromotion(clubId, promotionRequest);
        return  ResponseEntity.ok(promotion);
    }


    @GetMapping("/promotion/{clubId}")
    public ResponseEntity<List<PromotionResponse>> getPromotion(@PathVariable Long clubId) throws NotFoundException {
        List<PromotionResponse> promotionResponseList = promotionService.getAllPromotions(clubId);
        return  ResponseEntity.ok(promotionResponseList);
    }

    @PutMapping("/promotion/{promotionId}")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Long promotionId, @RequestBody PromotionRequest promotionRequest) throws NotFoundException {
        PromotionResponse promotion = promotionService.updatePromotion(promotionId, promotionRequest);
        return  ResponseEntity.ok(promotion);
    }

    @DeleteMapping("/promotion/{promotionId}")
    public ResponseEntity<String> deletePromotion(@PathVariable Long promotionId) throws NotFoundException {
        promotionService.deletePromotion(promotionId);
        return ResponseEntity.ok("Đã xóa thành công");
    }
    @GetMapping("/promotion/check")
    public ResponseEntity<PromotionResponse> checkValidPromotion(@RequestParam long clubId, @RequestParam String promotionCode){
        return ResponseEntity.ok(promotionService.checkPromotion(clubId,promotionCode));
    }
}
