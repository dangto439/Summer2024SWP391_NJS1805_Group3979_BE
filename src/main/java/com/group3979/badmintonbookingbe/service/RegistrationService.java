package com.group3979.badmintonbookingbe.service;


import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Contest;
import com.group3979.badmintonbookingbe.entity.Registration;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.RegistrationRequest;
import com.group3979.badmintonbookingbe.model.response.RegistrationResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.repository.IContestRepository;
import com.group3979.badmintonbookingbe.repository.IRegistrationRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {
    @Autowired
    IRegistrationRepository registrationRepository;
    @Autowired
    IContestRepository contestRepository;
    @Autowired
    AccountUtils accountUtils;
    @Autowired
    IAuthenticationRepository authenticationRepository;
    // register to participate in the contest
    public RegistrationResponse registrationContest(long contestId) {
        // get Date
        LocalDate registrationDate = LocalDate.now();
        // find contest
        Contest contest = contestRepository.findByContestId(contestId);
        if(contest == null) {
            throw new CustomException("Không tìm thấy cuộc thi với ID: " + contestId);
        }
        // get current account
        Account account = accountUtils.getCurrentAccount();
        // find registration by account to check duplicate
        Registration checkRegistration = registrationRepository.findRegistrationByAccountAndContest(account, contest);
        if(checkRegistration != null){
            throw new CustomException("Người chơi đã đăng ký tham gia cuộc thi này");
        }
        Registration registration = new Registration();
        registration.setContest(contest);
        registration.setAccount(account);
        registration.setRegistrationDate(registrationDate);

        registration = registrationRepository.save(registration);
        return buildRegistrationResponse(registration);
    }

    // get list of participants by contestId
    public List<Registration> getAllRegistrations(long contestId) {
        // find contest
        Contest contest = contestRepository.findByContestId(contestId);
        if(contest == null) {
            throw new CustomException("Không tìm thấy cuộc thi với ID: " + contestId);
        }
        return registrationRepository.findRegistrationsByContest(contest);
    }

    public Optional<Registration> getRegistrationById(long id) {
        return registrationRepository.findById(id);
    }

    // update registration
    public Registration updateRegistration(long registrationId, RegistrationRequest registrationRequest)  {
        Optional<Registration> existRegistration = getRegistrationById(registrationId);

        Contest contest = contestRepository.findByContestId(registrationRequest.getContestId());
        if(contest == null) {
            throw new CustomException("Không tìm thấy cuộc thi với ID: " + registrationRequest.getContestId());
        }

        Account player = authenticationRepository.findAccountById(registrationRequest.getPlayerId());
        if(player == null) {
            throw new CustomException("Không tìm thấy người chơi với ID: " + registrationRequest.getPlayerId());
        }

        if (existRegistration.isPresent()) {
            existRegistration.get().setContest(contestRepository.findByContestId(registrationRequest.getContestId()));
            existRegistration.get().setAccount(authenticationRepository.findAccountById(registrationRequest.getPlayerId()));

            return registrationRepository.save(existRegistration.get());
        }else {
            throw new CustomException("Không tìm thấy người đăng ký tham gia với ID: " + registrationId);
        }

    }

    public RegistrationResponse buildRegistrationResponse(Registration registration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return RegistrationResponse.builder()
                .registrationId(registration.getRegistrationId())
                .contest(registration.getContest())
                .account(registration.getAccount())
                .registrationDate(formatter.format(registration.getRegistrationDate()))
                .build();
    }
}
