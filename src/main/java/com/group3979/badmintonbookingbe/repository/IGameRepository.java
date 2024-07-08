package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Contest;
import com.group3979.badmintonbookingbe.entity.Court;
import com.group3979.badmintonbookingbe.entity.CourtSlot;
import com.group3979.badmintonbookingbe.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IGameRepository extends JpaRepository<Game,Long> {
    List<Game> findGamesByContest(Contest contest);
    Game findGameByGameId(long gameId);
    List<Game> findGamesByContest_ContestId(long contestId);

}
