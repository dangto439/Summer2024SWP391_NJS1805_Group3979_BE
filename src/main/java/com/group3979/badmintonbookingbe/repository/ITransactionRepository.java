package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
}
