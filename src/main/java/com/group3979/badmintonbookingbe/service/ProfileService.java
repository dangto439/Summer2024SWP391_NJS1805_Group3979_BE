package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.request.ProfileRequest;
import com.group3979.badmintonbookingbe.model.response.ProfileResponse;
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
                .email(account.getEmail())
                .gender(account.getGender())
                .role(account.getRole())
                .avatar(account.getAvatar())
                .build();
    }

    // update profile's current information
    public ProfileResponse updateProfile(ProfileRequest profileRequest) {
        Account account = accountUtils.getCurrentAccount();

        account.setName(profileRequest.getName());
        account.setPhone(profileRequest.getPhone());
        account.setEmail(profileRequest.getEmail());
        account.setGender(profileRequest.getGender());
        account.setAvatar(profileRequest.getAvatar());

        account = authenticationRepository.save(account);

        return ProfileResponse.builder()
                .name(account.getName())
                .phone(account.getPhone())
                .email(account.getEmail())
                .gender(account.getGender())
                .role(account.getRole())
                .avatar(account.getAvatar())
                .build();
    }
}
