package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingDetailRequest {
    private Long CourtSlot;
    private Date playingDate;
    private String promotionCode;
}
