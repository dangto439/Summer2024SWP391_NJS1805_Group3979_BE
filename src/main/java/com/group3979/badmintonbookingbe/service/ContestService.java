package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ContestStatus;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Contest;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.ContestRequest;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestService {
    @Autowired
    IClubRepository clubRepository;
    @Autowired
    IContestRepository contestRepository;
    public void createContest(ContestRequest contestRequest){
        Club club = clubRepository.findByClubId(contestRequest.getClubId());
        if(club != null){
            Contest contest = new Contest();
            contest.setCapacity(contestRequest.getCapacity());
            contest.setClub(club);
            contest.setFirstPrize(contestRequest.getFirstPrize());
            contest.setSecondPrize(contestRequest.getSecondPrize());
            contest.setUrlBanner(contestRequest.getUrlBanner());
            contest.setParticipationPrice(contestRequest.getParticipationPrice());
            contest.setStartDate(contestRequest.getStartDate());
            contest.setContestStatus(ContestStatus.ACTIVE);
            contestRepository.save(contest);
        }else {
            throw new CustomException("Câu lạc bộ không tồn tại");
        }
    }
}
