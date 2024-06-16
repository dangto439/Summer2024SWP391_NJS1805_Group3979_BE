package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlexibleBookingRequest {
    private String promotionCode;
    private long clubId;
    private int amountTime;
}
