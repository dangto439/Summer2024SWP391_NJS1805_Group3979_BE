package com.group3979.badmintonbookingbe.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.model.request.*;
import com.group3979.badmintonbookingbe.model.response.AccountResponse;
import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import com.group3979.badmintonbookingbe.model.response.StaffResponse;
import com.group3979.badmintonbookingbe.model.response.revenueResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.entity.Account;
import org.springframework.web.server.ResponseStatusException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    // xử lý logic
    @Autowired
    private IClubRepository clubRepository;
    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private WalletService walletService;
    @Autowired
    private IAuthenticationRepository authenticationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    @Value("${avatar.default.url}")
    private String avatatDefault;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.authenticationRepository.findAccountByEmail(email);
    }

    public List<AuthenticationResponse> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        List<AuthenticationResponse> authenticationResponses = new ArrayList<>();
        accounts = authenticationRepository.findAll();
        for (Account account : accounts) {
            authenticationResponses.add(AuthenticationResponse.builder()
                    .accountId(account.getId())
                    .phone(account.getPhone())
                    .email(account.getEmail())
                    .name(account.getName())
                    .role(account.getRole())
                    .gender(account.getGender())
                    .supervisorID(account.getSupervisorID())
                    .accountStatus(account.getAccountStatus())
                    .avatar(account.getAvatar())
                    .signupDate(account.getSignupDate())
                    .build());
        }
        return authenticationResponses;
    }

    public Account register(RegisterRequest registerRequest) throws SQLIntegrityConstraintViolationException {
        // check duplicate email and phone
        Account existEmailAccount = authenticationRepository.findAccountByEmail(registerRequest.getEmail());
        if(existEmailAccount != null) {
            throw new SQLIntegrityConstraintViolationException("Email đã tồn tại");
        }

        Account existPhoneAccount = authenticationRepository.findAccountByPhone(registerRequest.getPhone());
        if(existPhoneAccount != null) {
            throw new SQLIntegrityConstraintViolationException("Số điện thoại đã tồn tại");
        }

        // registerRequest: thông tin người dùng yêu cầu:
        // solve register logic
        LocalDate signupDate = LocalDate.now();
        Account account = new Account();
        account.setPhone(registerRequest.getPhone());
        account.setEmail(registerRequest.getEmail());
        account.setGender(registerRequest.getGender());
        account.setName(registerRequest.getName());
        account.setRole(registerRequest.getRole());
        account.setAvatar(avatatDefault);
        account.setSignupDate(signupDate);

        if (account.getRole().equals(Role.CUSTOMER)) {
            account.setAccountStatus(AccountStatus.ACTIVE);
        } else {
            account.setAccountStatus(AccountStatus.INACTIVE);
        }

        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // nhờ repository save xuống db
        account = authenticationRepository.saveAndFlush(account);
        walletService.createWallet(account.getEmail()); // after save account to DB, create a wallet
        emailService.sendMail(account.getEmail(), account.getName());
        return account;
    }

    public Account login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        Account account = authenticationRepository.findAccountByEmail(loginRequest.getEmail());
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            String token = tokenService.generateToken(account, 24 * 60 * 60 * 1000);
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.setId(account.getId());
            accountResponse.setPhone(account.getPhone());
            accountResponse.setName(account.getName());
            accountResponse.setEmail(account.getEmail());
            accountResponse.setRole(account.getRole());
            accountResponse.setSupervisorID(account.getSupervisorID());
            accountResponse.setGender(account.getGender());
            accountResponse.setAccountStatus(account.getAccountStatus());
            accountResponse.setSignupDate(account.getSignupDate());
            accountResponse.setToken(token);
            return accountResponse;
        } else if (account.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị khóa, vui lòng liên hệ với " +
                    "quản trị viên để biết thêm chi tiết.");
        }
        return null;
    }

    public AccountResponse loginGoogle(LoginGoogleRequest loginGoogleRequest) {
        AccountResponse accountResponse = new AccountResponse();
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
                account.setAvatar(avatatDefault);
                account.setGender(Gender.MALE); // default set khi create by login gg: MALE
                account.setSignupDate(LocalDate.now());
                walletService.createWallet(email);
                account = authenticationRepository.save(account);
            }
            accountResponse.setId(account.getId());
            accountResponse.setPhone(account.getPhone());
            accountResponse.setName(account.getName());
            accountResponse.setEmail(account.getEmail());
            accountResponse.setRole(account.getRole());
            accountResponse.setSupervisorID(account.getSupervisorID());
            accountResponse.setGender(account.getGender());
            accountResponse.setAccountStatus(account.getAccountStatus());
            accountResponse.setToken(tokenService.generateToken(account, 24 * 60 * 60 * 1000));
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        return accountResponse;
    }

    public AuthenticationResponse getAccountResponseByEmail(String email) {
        Account account = authenticationRepository.findAccountByEmail(email);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .phone(account.getPhone())
                .name(account.getName())
                .gender(account.getGender())
                .accountStatus(account.getAccountStatus())
                .signupDate(account.getSignupDate())
                .build();
        return authenticationResponse;
    }

    public void resetPassword(NewPasswordRequest newPasswordRequest) {

        Account account = accountUtils.getCurrentAccount();
        account.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
        authenticationRepository.save(account);

    }

    // register Account for Staff (Role = "STAFF")
    public StaffResponse registerStaff(StaffRegisterRequest staffRegisterRequest) throws BadRequestException, SQLIntegrityConstraintViolationException {
        // check duplicate email and phone
        Account existEmailAccount = authenticationRepository.findAccountByEmail(staffRegisterRequest.getEmail());
        if(existEmailAccount != null) {
            throw new SQLIntegrityConstraintViolationException("Email đã tồn tại");
        }

        Account existPhoneAccount = authenticationRepository.findAccountByPhone(staffRegisterRequest.getPhone());
        if(existPhoneAccount != null) {
            throw new SQLIntegrityConstraintViolationException("Số điện thoại đã tồn tại");
        }
        // solve logic
        Account staff = new Account();
        Account supervisor = accountUtils.getCurrentAccount();
        staff.setPhone(staffRegisterRequest.getPhone());
        staff.setEmail(staffRegisterRequest.getEmail());
        staff.setName(staffRegisterRequest.getName());
        staff.setGender(staffRegisterRequest.getGender());
        staff.setRole(Role.CLUB_STAFF);
        staff.setAccountStatus(AccountStatus.ACTIVE);
        staff.setPassword(passwordEncoder.encode(staffRegisterRequest.getPassword()));
        staff.setSupervisorID(supervisor.getId());
        staff.setAvatar(avatatDefault);
        staff.setSignupDate(LocalDate.now());
        walletService.createWallet(staffRegisterRequest.getEmail());
        Club club = clubRepository.findByClubId(staffRegisterRequest.getClubId());
        if (club == null) {
            throw new BadRequestException("Không tìm thấy câu lạc bộ!");
        }
        staff.setClub(club);

        staff = authenticationRepository.save(staff);

        return StaffResponse.builder()
                .staffId(staff.getId())
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .name(staff.getName())
                .gender(staff.getGender())
                .role(staff.getRole())
                .accountStatus(staff.getAccountStatus())
                .supervisorID(staff.getSupervisorID())
                .clubId(staff.getClub().getClubId())
                .signupDate(staff.getSignupDate())
                .build();
    }

    // view Club-Owner's staffs list
    public List<StaffResponse> getAllStaffs() {
        Long supervisorID = accountUtils.getCurrentAccount().getId();
        List<Account> staffsNeedToGet = new ArrayList<>();
        List<StaffResponse> staffResponses = new ArrayList<>();
        staffsNeedToGet.addAll(authenticationRepository.findClubStaffBySupervisorId(Role.CLUB_STAFF, supervisorID));
        for (Account staff : staffsNeedToGet) {
            staffResponses.add(StaffResponse.builder()
                    .staffId(staff.getId())
                    .phone(staff.getPhone())
                    .email(staff.getEmail())
                    .name(staff.getName())
                    .role(staff.getRole())
                    .gender(staff.getGender())
                    .supervisorID(staff.getSupervisorID())
                    .accountStatus(staff.getAccountStatus())
                    .clubId(staff.getClub().getClubId())
                    .signupDate(staff.getSignupDate())
                    .build());
        }
        return staffResponses;
    }

    // view staffs list by ClubId
    public List<StaffResponse> getAllStaffsByClub(Long clubId) {
        Club club = clubRepository.findByClubId(clubId);
        List<StaffResponse> staffResponses = new ArrayList<>();
        List<Account> staffsNeedToGet = new ArrayList<>();
        staffsNeedToGet.addAll(authenticationRepository.findClubStaffByClub(club));
        for (Account staff : staffsNeedToGet) {
            staffResponses.add(StaffResponse.builder()
                    .staffId(staff.getId())
                    .phone(staff.getPhone())
                    .email(staff.getEmail())
                    .name(staff.getName())
                    .role(staff.getRole())
                    .gender(staff.getGender())
                    .supervisorID(staff.getSupervisorID())
                    .accountStatus(staff.getAccountStatus())
                    .clubId(clubId)
                    .signupDate(staff.getSignupDate())
                    .build());
        }
        return staffResponses;
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

    //block or unblock Account by Admin
    public AuthenticationResponse blockUser(String email) {
        Account account = authenticationRepository.findAccountByEmail(email);
        if (account.getAccountStatus() == AccountStatus.ACTIVE) {
            account.setAccountStatus(AccountStatus.INACTIVE);
        } else {
            account.setAccountStatus(AccountStatus.ACTIVE);
        }
        authenticationRepository.save(account);
        return AuthenticationResponse.builder()
                .accountId(account.getId())
                .phone(account.getPhone())
                .email(account.getEmail())
                .name(account.getName())
                .role(account.getRole())
                .gender(account.getGender())
                .supervisorID(account.getSupervisorID())
                .accountStatus(account.getAccountStatus())
                .signupDate(account.getSignupDate())
                .build();
    }

    public Account getAccountById(Long id) {
        return authenticationRepository.findAccountById(id);
    }

    public Account UpdateAccount(AuthenticationResponse accountResponse, String password) {
        Account account = authenticationRepository.findAccountById(accountResponse.getAccountId());

        account.setAvatar(accountResponse.getAvatar());
        account.setAccountStatus(accountResponse.getAccountStatus());
        account.setPhone(accountResponse.getPhone());
        account.setEmail(accountResponse.getEmail());
        account.setName(accountResponse.getName());
        account.setRole(accountResponse.getRole());
        account.setGender(accountResponse.getGender());
        account.setPassword(passwordEncoder.encode(password));
        return authenticationRepository.save(account);
    }

    //thong ke account cho admin
    public List<revenueResponse> getRevenueResponse(int year) {
        return authenticationRepository.countSignupsByMonthForYear(year);
    }
}

