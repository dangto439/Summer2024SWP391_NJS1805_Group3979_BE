package com.group3979.badmintonbookingbe.service;

import java.util.List;
import java.util.Optional;

import com.group3979.badmintonbookingbe.model.AccountReponse;
import com.group3979.badmintonbookingbe.model.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3979.badmintonbookingbe.Repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.LoginRequest;
import com.group3979.badmintonbookingbe.model.RegisterRequest;

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
        account.setName(registerRequest.getName());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // nhờ repository save xuống db
        return authenticationRepository.save(account);
    }

    public Account login(LoginRequest loginRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getPhone(), loginRequest.getPassword()));

        Account account = authenticationRepository.findAccountByPhone(loginRequest.getPhone());
        String token = tokenService.generateToken(account);
        AccountReponse accountReponse = new AccountReponse();
        accountReponse.setPhone(account.getPhone());
        accountReponse.setToken(token);
        return accountReponse;
    }


    public Account resetpassword(String email) {

        Account account = authenticationRepository.findAccountByEmail(email);
        String token = tokenService.generateToken(account);
        AccountReponse accountReponse = new AccountReponse();
        //accountReponse.setPassword(account.getPhone());
        accountReponse.setToken(token);
        return accountReponse;

    }


}
