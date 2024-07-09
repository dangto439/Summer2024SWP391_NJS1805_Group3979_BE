package com.group3979.badmintonbookingbe.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private int month;
    private double sumamount;
}
