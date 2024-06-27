package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Transaction;
import com.group3979.badmintonbookingbe.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsBySenderWallet(Wallet wallet);
    Transaction findTransactionByTransactionId(long id);
}
