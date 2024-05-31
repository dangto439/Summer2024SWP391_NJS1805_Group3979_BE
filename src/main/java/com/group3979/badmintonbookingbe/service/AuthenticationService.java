package com.group3979.badmintonbookingbe.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Role;
import com.group3979.badmintonbookingbe.exception.AuthException;
import com.group3979.badmintonbookingbe.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.entity.Account;

@Service
public class AuthenticationService implements UserDetailsService {

    // xử lý logic 
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IAuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        return this.authenticationRepository.findAccountByPhone(phone);
    }

    public Account register(RegisterRequest registerRequest) {
        //registerRequest:  thông tin người dùng  yêu cầu:
        // solve register logic
        Account account = new Account();
        account.setPhone(registerRequest.getPhone());
        account.setEmail(registerRequest.getEmail());
        account.setGender(registerRequest.getGender());
        account.setName(registerRequest.getName());
        account.setRole(registerRequest.getRole());
        if (account.getRole().equals(Role.CUSTOMER)) {
            account.setAccountStatus(AccountStatus.ACTIVE);
        } else {
            account.setAccountStatus(AccountStatus.INACTIVE);
        }

        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // nhờ repository save xuống db
        account = authenticationRepository.save(account);
        emailService.sendMail(account.getEmail(), account.getName());
        return account;
    }

    public Account login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getPhone(), loginRequest.getPassword()));
        Account account = authenticationRepository.findAccountByPhone(loginRequest.getPhone());
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            String token = tokenService.generateToken(account);
            AccountReponse accountReponse = new AccountReponse();
            accountReponse.setPhone(account.getPhone());
            accountReponse.setName(account.getName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setToken(token);
            return accountReponse;
        }
        return null;
    }

    public AccountReponse loginGoogle(LoginGoogleRequest loginGoogleRequest) throws FirebaseAuthException {
        AccountReponse accountReponse = new AccountReponse();

            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(loginGoogleRequest.getToken());
            String email = firebaseToken.getEmail();
            Account account = authenticationRepository.findAccountByEmail(email);
            if (account == null) {
                account.setName(firebaseToken.getName());
                account.setEmail(email);
                account.setRole(Role.CUSTOMER);
                account.setAccountStatus(AccountStatus.ACTIVE);
                account = authenticationRepository.save(account);
            }
            accountReponse.setId(account.getId());
            accountReponse.setName(account.getName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setAccountStatus(account.getAccountStatus());
            accountReponse.setRole(account.getRole());
            accountReponse.setGender(account.getGender());
            accountReponse.setToken(tokenService.generateToken(account));

        return accountReponse;
    }


    public void resetPassword(NewPasswordRequest newPasswordRequest) {
        boolean isTokenExpired = tokenService.isTokenExpired(newPasswordRequest.getToken());
        if (!isTokenExpired) {
            Account account = tokenService.extractAccount(newPasswordRequest.getToken());
            account.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
            authenticationRepository.save(account);

        } else {
            throw new AuthException("Expired Token!");
        }
    }
}
