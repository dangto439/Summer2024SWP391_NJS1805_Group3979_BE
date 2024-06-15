package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.DiscountRuleRequest;
import com.group3979.badmintonbookingbe.model.response.DiscountRuleResponse;
import com.group3979.badmintonbookingbe.service.DiscountRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class DiscountRuleAPI {
    @Autowired
    DiscountRuleService discountRuleService;

    @PostMapping("/discount-rule/{clubId}")
    public ResponseEntity addDiscountRule(@PathVariable Long clubId, @RequestBody DiscountRuleRequest discountRuleRequest) {
        DiscountRuleResponse discountRule = discountRuleService.createDiscountRule(clubId, discountRuleRequest);
        return ResponseEntity.ok(discountRule);
    }

    @GetMapping("/discount-rule/{clubId}")
    public ResponseEntity getDiscountRule(@PathVariable Long clubId) {
        DiscountRuleResponse discountRule = discountRuleService.getDiscountRule(clubId);
        return ResponseEntity.ok(discountRule);
    }

    @PutMapping("/discount-rule/{clubId}")
    public ResponseEntity updateDiscountRule(@PathVariable Long clubId, @RequestBody DiscountRuleRequest discountRuleRequest) {
        DiscountRuleResponse discountRule = discountRuleService.updateDiscountRule(clubId, discountRuleRequest);
        return ResponseEntity.ok(discountRule);
    }

    @DeleteMapping("/discount-rule/{clubId}")
    public ResponseEntity deleteDiscountRule(@PathVariable Long clubId) {
        discountRuleService.deleteDiscountRule(clubId);
        return ResponseEntity.ok().build();
    }
}
