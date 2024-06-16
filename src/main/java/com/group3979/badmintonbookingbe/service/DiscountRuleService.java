package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.DiscountRule;
import com.group3979.badmintonbookingbe.model.request.DiscountRuleRequest;
import com.group3979.badmintonbookingbe.model.response.DiscountRuleResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IDiscountRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountRuleService {
    @Autowired
    IClubRepository clubRepository;

    @Autowired
    IDiscountRuleRepository discountRuleRepository;
    // create discountRule by clubId
    public DiscountRuleResponse createDiscountRule(Long clubId, DiscountRuleRequest discountRuleRequest){
        Club club = clubRepository.findByClubId(clubId);
        DiscountRule discountRule = new DiscountRule();
        discountRule.setClub(club);
        discountRule.setFlexiblePercent(discountRuleRequest.getFlexiblePercent());
        discountRule.setFixedPercent(discountRuleRequest.getFixedPercent());

        discountRule = discountRuleRepository.save(discountRule);
        return DiscountRuleResponse.builder()
                .discountRuleId(discountRule.getDiscountRuleId())
                .flexiblePercent(discountRule.getFlexiblePercent())
                .fixedPercent(discountRule.getFixedPercent())
                .clubId(clubId)
                .clubName(club.getClubName())
                .build();
    }

    // get information of discountRule by clubId
    public DiscountRuleResponse getDiscountRule(Long clubId){
        Club club = clubRepository.findByClubId(clubId);
        DiscountRule discountRule = discountRuleRepository.findDiscountRuleByClub(club);

        return DiscountRuleResponse.builder()
                .discountRuleId(discountRule.getDiscountRuleId())
                .flexiblePercent(discountRule.getFlexiblePercent())
                .fixedPercent(discountRule.getFixedPercent())
                .clubId(clubId)
                .clubName(club.getClubName())
                .build();
    }

    // update discountRule(flexPercent and fixedPercent) by clubId
    public DiscountRuleResponse updateDiscountRule(Long clubId, DiscountRuleRequest discountRuleRequest){
        Club club = clubRepository.findByClubId(clubId);
        DiscountRule discountRule = discountRuleRepository.findDiscountRuleByClub(club);
        discountRule.setFlexiblePercent(discountRuleRequest.getFlexiblePercent());
        discountRule.setFixedPercent(discountRuleRequest.getFixedPercent());
        discountRule = discountRuleRepository.save(discountRule);

        return DiscountRuleResponse.builder()
                .discountRuleId(discountRule.getDiscountRuleId())
                .flexiblePercent(discountRule.getFlexiblePercent())
                .fixedPercent(discountRule.getFixedPercent())
                .clubId(clubId)
                .clubName(club.getClubName())
                .build();
    }

    public void deleteDiscountRule(Long clubId){
        Club club = clubRepository.findByClubId(clubId);
        DiscountRule discountRule = discountRuleRepository.findDiscountRuleByClub(club);
        discountRuleRepository.delete(discountRule);
    }
}
