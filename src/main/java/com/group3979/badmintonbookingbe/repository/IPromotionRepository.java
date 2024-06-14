package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByClub(Club club);
    Promotion findByPromotionId(Long promotionId);
}
