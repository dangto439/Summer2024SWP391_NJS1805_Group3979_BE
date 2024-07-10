package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ContestRequest {
    private double participationPrice;
    private int capacity;
    private double firstPrize;
    private double secondPrize;
    private LocalDate startDate;
    private LocalDate endDate;
    private long clubId;
    private String urlBanner;
    private String name;
}
