package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Transaction;
import com.group3979.badmintonbookingbe.entity.Wallet;
import com.group3979.badmintonbookingbe.model.response.RevenueResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsBySenderWallet(Wallet wallet);
    Transaction findTransactionByTransactionId(long id);

    //JPQL
    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(MONTH(t.timestamp), SUM(t.amount)) " +
            "FROM Transaction t WHERE t.type = 'DEPOSIT' AND YEAR(t.timestamp) = :year " +
            "GROUP BY MONTH(t.timestamp) ORDER BY MONTH(t.timestamp)")
    List<RevenueResponse> findRevenueResponseByYear(@Param("year") int year);


    @Query("SELECT new com.group3979.badmintonbookingbe.model.response.RevenueResponse(WEEK(t.timestamp), SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.type = 'DEPOSIT' AND YEAR(t.timestamp) = :year AND MONTH(t.timestamp) = :month " +
            "GROUP BY WEEK(t.timestamp) " +
            "ORDER BY WEEK(t.timestamp)")
    List<RevenueResponse> findWeeklyRevenueByMonthAndYear(@Param("year") int year, @Param("month") int month);


}

