package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ContestStatus;
import com.group3979.badmintonbookingbe.model.request.TransferContestRequest;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.ContestRequest;
import com.group3979.badmintonbookingbe.model.request.UpdateContestRequest;
import com.group3979.badmintonbookingbe.model.response.ContestResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IContestRepository;
import com.group3979.badmintonbookingbe.repository.IRegistrationRepository;
import com.group3979.badmintonbookingbe.repository.IWalletRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContestService {
    @Autowired
    IClubRepository clubRepository;
    @Autowired
    IContestRepository contestRepository;
    @Autowired
    GameService gameService;
    @Autowired
    AccountUtils accountUtils;
    @Autowired
    WalletService walletService;
    @Autowired
    IRegistrationRepository registrationRepository;
    @Autowired
    IWalletRepository walletRepository;

    public ContestResponse createContest(ContestRequest contestRequest) {
        Club club = clubRepository.findByClubId(contestRequest.getClubId());
        if (club != null) {
            if(isLog2(contestRequest.getCapacity())){
                if(checkPercentPrice(contestRequest,club)){
                    Contest contest = new Contest();
                    contest.setCapacity(contestRequest.getCapacity());
                    contest.setClub(club);
                    contest.setFirstPrize(contestRequest.getFirstPrize());
                    contest.setSecondPrize(contestRequest.getSecondPrize());
                    contest.setUrlBanner(contestRequest.getUrlBanner());
                    contest.setParticipationPrice(contestRequest.getParticipationPrice());
                    contest.setStartDate(contestRequest.getStartDate());
                    contest.setEndDate(contestRequest.getEndDate());
                    contest.setName(contestRequest.getName());
                    contest = contestRepository.save(contest);
                    gameService.createMatchesContest(contest.getCapacity(), contest);
                    return this.buildContestResponse(contest);
                }else {
                    throw new CustomException("Số dư không đủ để tạo giải đấu.");
                }
            }else {
                throw new CustomException("Vui lòng nhập số là lũy thừa của 2 cho số người tham gia.");
            }
        } else {
            throw new CustomException("Câu lạc bộ không tồn tại");
        }
    }

    public boolean checkPercentPrice(ContestRequest contestRequest, Club club) {
        double balanceClubOwner = walletRepository.findWalletByAccount(club.getAccount()).getBalance();
        double fivePercentPrice = contestRequest.getParticipationPrice()*contestRequest.getCapacity()*0.05;
        return balanceClubOwner > fivePercentPrice;
    }
    public boolean isLog2(int n) {
        if (n < 2 ) {
            return false;
        }

        double log2 = Math.log(n) / Math.log(2);
        return Math.ceil(log2) == Math.floor(log2);
    }

    public ContestResponse updateContest(UpdateContestRequest updateContestRequest) {
        Club club = clubRepository.findByClubId(updateContestRequest.getClubId());
        Contest contest = contestRepository.findByContestId(updateContestRequest.getContestId());
        if (club != null) {
            if (contest != null) {
                contest.setClub(club);
                contest.setEndDate(updateContestRequest.getEndDate());
                contest.setFirstPrize(updateContestRequest.getFirstPrize());
                contest.setSecondPrize(updateContestRequest.getSecondPrize());
                contest.setUrlBanner(updateContestRequest.getUrlBanner());
                contest.setParticipationPrice(updateContestRequest.getParticipationPrice());
                contest.setStartDate(updateContestRequest.getStartDate());
                contest.setEndDate(updateContestRequest.getEndDate());
                contest.setContestStatus(ContestStatus.ACTIVE);
                contest.setName(updateContestRequest.getName());
                contest = contestRepository.save(contest);
                return this.buildContestResponse(contest);
            } else {
                throw new CustomException("Cuộc thi đấu không tồn tại");
            }
        } else {
            throw new CustomException("Câu lạc bộ không tồn tại");
        }
    }

    public ContestResponse getContestById(long id) {
        return this.buildContestResponse(contestRepository.findByContestId(id));
    }

    public ContestResponse buildContestResponse(Contest contest) {
        return ContestResponse.builder()
                .endDate(contest.getEndDate())
                .clubId(contest.getClub().getClubId())
                .contestId(contest.getContestId())
                .firstPrize(contest.getFirstPrize())
                .secondPrize(contest.getSecondPrize())
                .startDate(contest.getStartDate())
                .status(contest.getContestStatus())
                .urlBanner(contest.getUrlBanner())
                .capacity(contest.getCapacity())
                .participationPrice(contest.getParticipationPrice())
                .name(contest.getName())
                .build();
    }

    public List<ContestResponse> getHotContest() {
        List<ContestResponse> contestResponses = new ArrayList<>();
        List<Contest> contests = contestRepository.findHotContest();
        for(Contest contest: contests){
            contestResponses.add(this.buildContestResponse(contest));
        }
        return contestResponses;
    }

    public List<ContestResponse> getContestsCurrentAccount() {

            Account account = accountUtils.getCurrentAccount();
            List<Contest> contests = contestRepository.findContestsByClub_Account(account);

                List<ContestResponse> contestResponses = new ArrayList<>();
                for(Contest contest:contests){
                    contestResponses.add(this.buildContestResponse(contest));
                }
                return contestResponses;
    }

    // cancel Contest
    public void cancelContest(long contestId) throws NotFoundException {
        Contest contest = contestRepository.findByContestId(contestId);
        contest.setContestStatus(ContestStatus.INACTIVE);
        Wallet clubOwnerWallet = walletService.getWalletForClubOwner(contest.getClub().getClubId());

        if(contest.getContestStatus().equals(ContestStatus.INACTIVE)){
            List<Registration> registrationList = registrationRepository.findRegistrationsByContest(contest);
            for (Registration registration : registrationList){
                TransferContestRequest transferContestRequest = new TransferContestRequest();
                transferContestRequest.setContestId(contestId);
                transferContestRequest.setReceiverWalletId(walletRepository.findWalletIdByAccount(registration.getAccount()));
                transferContestRequest.setSenderWalletId(clubOwnerWallet.getWalletId());
                transferContestRequest.setAmount(contest.getParticipationPrice());
                walletService.refundOnContest(transferContestRequest);
            }
        }
    }

    public List<ContestResponse> getAllContest(){
        List<ContestResponse> contestResponses = new ArrayList<>();
        List<Contest> contests = contestRepository.findAll();
        for(Contest contest: contests){
            contestResponses.add(this.buildContestResponse(contest));
        }
        return contestResponses;
    }

}
