package com.group3979.badmintonbookingbe.model;

import lombok.Data;

@Data
public class ClubRequest {
    private String clubName;
    private String clubDescription;
    private String clubAddress;
    private String clubhotline;
    private int capacity;
    private int openingTime;
    private int closingTime;
}
