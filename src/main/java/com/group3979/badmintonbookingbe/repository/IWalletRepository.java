package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findWalletByAccount(Account account);
    Wallet findWalletByWalletId(Long id);
    long findWalletIdByAccount(Account account);
}
