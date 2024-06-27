package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class PromotionRequest {
    private String promotionCode;
    private double discount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus status;
}
