package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class PromotionRequest {
    private String promotionCode;
    private double discount;
    private Date startDate;
    private Date endDate;
    private PromotionStatus status;
}
