package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.entity.Account;
import lombok.Data;

@Data
public class AccountReponse extends Account {
    private String token;
}