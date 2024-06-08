package com.group3979.badmintonbookingbe.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClubRequest {
    private String clubName;
    private String clubDescription;
    private String clubAddress;
    private String clubHotLine;
    private int capacity; // sl court
    private int openingTime;
    private int closingTime;
    private List<String> urlImages;
}
