package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.eNum.ContestStatus;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IContestRepository extends JpaRepository<Contest,Long> {
    Contest findByContestId(Long contestId);
    List<Contest> findContestsByClub_Account(Account account);
    List<Contest> findContestsByContestStatus(ContestStatus contestStatus);
    @Query("SELECT a \n" +
            "FROM Contest a \n" +
            "ORDER BY a.firstPrize DESC")
    List<Contest> findHotContest();
}
