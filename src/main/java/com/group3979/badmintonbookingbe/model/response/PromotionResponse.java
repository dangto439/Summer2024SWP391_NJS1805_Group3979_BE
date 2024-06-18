package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {
    private Long promotionId;
    private String promotionCode;
    private double discount;
    private String startDate;
    private String endDate;
    private PromotionStatus promotionStatus;
}
