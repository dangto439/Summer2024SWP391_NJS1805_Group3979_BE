package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CourtSlotRequest {
    private Long clubId;
    private float price;
    RushHourRequest rushHourRequest;
}