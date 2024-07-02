package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IScoreRepository extends JpaRepository<Score,Long> {
}
