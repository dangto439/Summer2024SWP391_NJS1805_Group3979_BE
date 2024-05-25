package com.group3979.badmintonbookingbe.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3979.badmintonbookingbe.entity.Account;

@Repository
public interface IAuthenticationRepository extends JpaRepository<Account, Long> {
}
