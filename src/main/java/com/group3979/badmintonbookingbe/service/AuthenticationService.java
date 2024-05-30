package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Role;
import com.group3979.badmintonbookingbe.model.AccountReponse;
import com.group3979.badmintonbookingbe.model.AuthenticationResponse;
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
        //registerRequest:  thông tin người dùng yêu cầu:
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
        return authenticationRepository.save(account);
    }

    public Account login(LoginRequest loginRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getPhone(), loginRequest.getPassword()));

        Account account = authenticationRepository.findAccountByPhone(loginRequest.getPhone());
        String token = tokenService.generateToken(account);
        AccountReponse accountReponse = new AccountReponse();
        accountReponse.setPhone(account.getPhone());
        accountReponse.setName(account.getName());
        accountReponse.setEmail(account.getEmail());
        accountReponse.setToken(token);
        return accountReponse;
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        authenticationRepository.save(account);
    }

    // register Account for Staff (Role = "STAFF")
    public AuthenticationResponse registerStaff(RegisterRequest registerRequest) {
        Account staff = new Account();
        staff.setPhone(registerRequest.getPhone());
        staff.setEmail(registerRequest.getEmail());
        staff.setName(registerRequest.getName());
        staff.setGender(registerRequest.getGender());
        staff.setRole(Role.CLUB_STAFF);
        staff.setAccountStatus(AccountStatus.ACTIVE);
        staff.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        staff = authenticationRepository.save(staff);

        return AuthenticationResponse.builder()
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .name(staff.getName())
                .gender(staff.getGender())
                .role(staff.getRole())
                .accountStatus(staff.getAccountStatus())
                .build();
    }

}
