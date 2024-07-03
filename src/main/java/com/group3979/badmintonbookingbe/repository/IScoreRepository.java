package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Game;
import com.group3979.badmintonbookingbe.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findScoresByGame(Game game);
}
