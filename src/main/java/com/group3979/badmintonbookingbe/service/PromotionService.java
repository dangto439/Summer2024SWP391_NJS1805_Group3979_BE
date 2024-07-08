package com.group3979.badmintonbookingbe.service;


import com.group3979.badmintonbookingbe.eNum.PromotionStatus;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Promotion;
import com.group3979.badmintonbookingbe.model.request.PromotionRequest;
import com.group3979.badmintonbookingbe.model.response.PromotionResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IPromotionRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;


@Service
public class PromotionService {
    @Autowired
    IPromotionRepository promotionRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    IClubRepository clubRepository;

    public PromotionResponse createPromotion(Long clubId, PromotionRequest promotionRequest) {
        //Format Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Promotion promotion = new Promotion();
        promotion.setPromotionCode(promotionRequest.getPromotionCode());
        promotion.setDiscount(promotionRequest.getDiscount());
        promotion.setStartDate(promotionRequest.getStartDate());
        promotion.setEndDate(promotionRequest.getEndDate());
        promotion.setPromotionStatus(PromotionStatus.ACTIVE);
        promotion.setClub(clubRepository.findByClubId(clubId));

        promotion = promotionRepository.save(promotion);

        PromotionResponse promotionResponse = PromotionResponse.builder()
                .promotionId(promotion.getPromotionId())
                .promotionCode(promotion.getPromotionCode())
                .discount(promotion.getDiscount())
                .startDate(formatter.format(promotion.getStartDate()))
                .endDate(formatter.format(promotion.getEndDate()))
                .promotionStatus(promotion.getPromotionStatus())
                .build();

        return promotionResponse;
    }

    public List<PromotionResponse> getAllPromotions(Long clubId) {
        Club club = clubRepository.findByClubId(clubId);
        List<Promotion> promotionList = promotionRepository.findByClub(club);
        List<PromotionResponse> promotionResponseList = new ArrayList<>();

        //Format Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Promotion promotion : promotionList) {
            PromotionResponse promotionResponse = PromotionResponse.builder()
                    .promotionId(promotion.getPromotionId())
                    .promotionCode(promotion.getPromotionCode())
                    .discount(promotion.getDiscount())
                    .startDate(formatter.format(promotion.getStartDate()))
                    .endDate(formatter.format(promotion.getEndDate()))
                    .promotionStatus(promotion.getPromotionStatus())
                    .build();

            promotionResponseList.add(promotionResponse);
        }
        return promotionResponseList;
    }

    public PromotionResponse updatePromotion(Long promotionId, PromotionRequest promotionRequest) {
        //Format Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Promotion promotion = promotionRepository.findByPromotionId(promotionId);
        if (promotion != null) {
            promotion.setPromotionCode(promotionRequest.getPromotionCode());
            promotion.setDiscount(promotionRequest.getDiscount());
            promotion.setStartDate(promotionRequest.getStartDate());
            promotion.setEndDate(promotionRequest.getEndDate());
            promotion.setPromotionStatus(promotionRequest.getStatus());

            promotion = promotionRepository.save(promotion);
        }
        PromotionResponse promotionResponse = PromotionResponse.builder()
                .promotionId(promotion.getPromotionId())
                .promotionCode(promotion.getPromotionCode())
                .discount(promotion.getDiscount())
                .startDate(formatter.format(promotion.getStartDate()))
                .endDate(formatter.format(promotion.getEndDate()))
                .promotionStatus(promotion.getPromotionStatus())
                .build();

        return promotionResponse;
    }
    public Promotion checkValidPromotion(long clubId, String promotionCode){
        Promotion promotion = promotionRepository.findPromotionByPromotionCode(promotionCode);
        if(promotion != null && clubId == promotion.getClub().getClubId()
                && promotion.getPromotionStatus().equals(PromotionStatus.ACTIVE)){
            return promotion;
        }
        return null;
    }
}
