package com.group3979.badmintonbookingbe.utils;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.AccountReponse;
import com.group3979.badmintonbookingbe.model.AuthenticationResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {
    public Account getCurrentAccount(){
       return (Account)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }
}
