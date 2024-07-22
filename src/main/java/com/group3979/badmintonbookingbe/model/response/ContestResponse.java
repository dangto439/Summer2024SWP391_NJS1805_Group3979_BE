package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.ContestStatus;
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
    private ContestStatus status;
    private double secondPrize;
    private LocalDate startDate;
    private LocalDate endDate;
    private long clubId;
    private String urlBanner;
}
