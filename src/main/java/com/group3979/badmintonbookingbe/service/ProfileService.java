package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.ProfileResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    IAuthenticationRepository authenticationRepository;

    @Autowired
    AccountUtils accountUtils;

    // get profile's current information
    public ProfileResponse getCurrentProfile() {
        Account account = accountUtils.getCurrentAccount();
        return ProfileResponse.builder()
                .name(account.getName())
                .phone(account.getPhone())
                .password(account.getPassword())
                .email(account.getEmail())
                .gender(account.getGender())
                .role(account.getRole())
                .avatar(account.getAvatar())
                .build();
    }
}
