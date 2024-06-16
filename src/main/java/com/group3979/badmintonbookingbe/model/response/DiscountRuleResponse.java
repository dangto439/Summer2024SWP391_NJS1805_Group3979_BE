package com.group3979.badmintonbookingbe.model.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRuleResponse {
    private Long discountRuleId;
    private double flexiblePercent;
    private double fixedPercent;
    private Long clubId;
    private String clubName;
}
