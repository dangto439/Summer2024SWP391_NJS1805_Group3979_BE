package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDiscountRuleRepository extends JpaRepository<DiscountRule, Long> {
    DiscountRule findDiscountRuleByClub(Club club);
}
