package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FixedBookingRequest {
    private int year;
    private int month;
    private long clubId;
    private String promotionCode;
    //Sunday: 1, Monday:2, Tuesday:3, Wednesday: 4, Thursday: 5, Friday:6, Saturday: 7
    private List<Integer> dayOfWeeks;
    private List<Long> SlotIds;
}
