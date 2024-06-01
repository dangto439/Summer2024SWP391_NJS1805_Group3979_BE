package com.group3979.badmintonbookingbe.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Role;
import com.group3979.badmintonbookingbe.exception.AuthException;
import com.group3979.badmintonbookingbe.model.request.LoginGoogleRequest;
import com.group3979.badmintonbookingbe.model.request.LoginRequest;
import com.group3979.badmintonbookingbe.model.request.NewPasswordRequest;
import com.group3979.badmintonbookingbe.model.request.RegisterRequest;
import com.group3979.badmintonbookingbe.model.response.AccountReponse;
import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    // xử lý logic
    @Autowired
    private AccountUtils accountUtils;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.authenticationRepository.findAccountByEmail(email);
    }

    public Account register(RegisterRequest registerRequest) {
        // registerRequest: thông tin người dùng yêu cầu:
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
                loginRequest.getEmail(), loginRequest.getPassword()));
        Account account = authenticationRepository.findAccountByEmail(loginRequest.getEmail());
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            String token = tokenService.generateToken(account,24*60*60*1000);
            AccountReponse accountReponse = new AccountReponse();
            accountReponse.setId(account.getId());
            accountReponse.setPhone(account.getPhone());
            accountReponse.setName(account.getName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setRole(account.getRole());
            accountReponse.setSupervisorID(account.getSupervisorID());
            accountReponse.setGender(account.getGender());
            accountReponse.setAccountStatus(account.getAccountStatus());
            accountReponse.setToken(token);
            return accountReponse;
        }
        return null;
    }

    public AccountReponse loginGoogle(LoginGoogleRequest loginGoogleRequest) {
        AccountReponse accountReponse = new AccountReponse();
        System.out.println(loginGoogleRequest.getToken());
        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(loginGoogleRequest.getToken());
            String email = firebaseToken.getEmail();
            Account account = authenticationRepository.findAccountByEmail(email);
            if (account == null) {
                account = new Account();
                account.setName(firebaseToken.getName());
                account.setEmail(email);
                account.setRole(Role.CUSTOMER);
                account.setAccountStatus(AccountStatus.ACTIVE);
                account = authenticationRepository.save(account);
            }
            accountReponse.setId(account.getId());
            accountReponse.setPhone(account.getPhone());
            accountReponse.setName(account.getName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setRole(account.getRole());
            accountReponse.setSupervisorID(account.getSupervisorID());
            accountReponse.setGender(account.getGender());
            accountReponse.setAccountStatus(account.getAccountStatus());
            accountReponse.setToken(tokenService.generateToken(account,24*60*60*1000));
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
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

    // register Account for Staff (Role = "STAFF")
    public AuthenticationResponse registerStaff(RegisterRequest registerRequest) {
        Account staff = new Account();
        Account supervisor = accountUtils.getCurrentAccount();
        staff.setPhone(registerRequest.getPhone());
        staff.setEmail(registerRequest.getEmail());
        staff.setName(registerRequest.getName());
        staff.setGender(registerRequest.getGender());
        staff.setRole(Role.CLUB_STAFF);
        staff.setAccountStatus(AccountStatus.ACTIVE);
        staff.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        staff.setSupervisorID(supervisor.getId());

        staff = authenticationRepository.save(staff);

        return AuthenticationResponse.builder()
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .name(staff.getName())
                .gender(staff.getGender())
                .role(staff.getRole())
                .accountStatus(staff.getAccountStatus())
                .supervisorID(staff.getSupervisorID())
                .build();
    }

    // view Club-Owner's staffs list
    public List<Account> getAllStaffs() {
        Long supervisorID = accountUtils.getCurrentAccount().getId();
        List<Account> staffsNeedToGet = new ArrayList<>();
        staffsNeedToGet.addAll(authenticationRepository.findClubStaffBySupervisorId(Role.CLUB_STAFF, supervisorID));
        return staffsNeedToGet;
    }

    // block Staff by Club-Owner
    public boolean blockStaff(Long idBlocked) {
        Long supervisorID = accountUtils.getCurrentAccount().getId();
        List<Account> staffsList = authenticationRepository.findClubStaffBySupervisorId(Role.CLUB_STAFF, supervisorID);

        for (Account staff : staffsList) {
            if (staff.getId() == idBlocked) {
                staff.setAccountStatus(AccountStatus.INACTIVE);
                authenticationRepository.save(staff);
                return true;
            }
        }
        return false;
    }
}
