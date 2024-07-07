package com.group3979.badmintonbookingbe.model.response;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestResponse {
    private long contestId;
    private double participationPrice;
    private int capacity;
    private String name;
    private double firstPrize;
    private double secondPrize;
    private LocalDate startDate;
    private long clubId;
    private String urlBanner;
}
