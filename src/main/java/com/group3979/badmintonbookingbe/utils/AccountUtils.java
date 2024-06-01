package com.group3979.badmintonbookingbe.utils;

import com.group3979.badmintonbookingbe.entity.Account;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {
    public Account getCurrentAccount() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }
}
