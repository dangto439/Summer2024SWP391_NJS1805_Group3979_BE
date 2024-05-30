package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.eNum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3979.badmintonbookingbe.entity.Account;

import java.util.List;

@Repository
public interface IAuthenticationRepository extends JpaRepository<Account, Long> {
    Account findAccountByPhone(String phone);
    Account findAccountByEmail(String email);
    List<Account> findAccountByRole(Role role);
//    Account findAccountById(Long id);
}
