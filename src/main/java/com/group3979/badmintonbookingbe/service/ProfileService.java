package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.ChangePasswordRequest;
import com.group3979.badmintonbookingbe.model.ProfileRequest;
import com.group3979.badmintonbookingbe.model.ProfileResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    IAuthenticationRepository authenticationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
    public ProfileResponse updateProfile(ProfileRequest profileRequest){
        Account account = accountUtils.getCurrentAccount();

        account.setName(profileRequest.getName());
        account.setPhone(profileRequest.getPhone());
        account.setGender(profileRequest.getGender());
        account.setAvatar(profileRequest.getAvatar());

        account = authenticationRepository.save(account);

        return ProfileResponse.builder()
                .name(account.getName())
                .phone(account.getPhone())
                .gender(account.getGender())
                .role(account.getRole())
                .avatar(account.getAvatar())
                .build();
    }

    // change profile's password
    public ProfileResponse changePassword(ChangePasswordRequest changePasswordRequest) throws BadRequestException {
        Account account = accountUtils.getCurrentAccount();

        if(passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())){
            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            account = authenticationRepository.save(account);

            return ProfileResponse.builder()
                    .name(account.getName())
                    .phone(account.getPhone())
                    .email(account.getEmail())
                    .gender(account.getGender())
                    .password(account.getPassword())
                    .role(account.getRole())
                    .avatar(account.getAvatar())
                    .build();
        }else {
            throw new BadRequestException("Passwords do not match");
        }
    }
}
