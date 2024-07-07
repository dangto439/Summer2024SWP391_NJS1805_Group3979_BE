package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UpdateContestRequest {
    private long contestId;
    private String name;
    private double participationPrice;
    private double firstPrize;
    private double secondPrize;
    private LocalDate startDate;
    private long clubId;
    private String urlBanner;
}
