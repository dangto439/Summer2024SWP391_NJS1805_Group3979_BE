package com.group3979.badmintonbookingbe.model.response;

import com.group3979.badmintonbookingbe.entity.Account;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AccountResponse extends Account {
    private String token;
}
