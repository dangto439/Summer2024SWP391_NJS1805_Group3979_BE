package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.eNum.Role;
import com.group3979.badmintonbookingbe.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3979.badmintonbookingbe.entity.Account;

import java.util.List;

@Repository
public interface IAuthenticationRepository extends JpaRepository<Account, Long> {
    Account findAccountByEmail(String email);
    Account findAccountById(Long id);
    @Query("SELECT a FROM Account a WHERE a.role = :role AND a.supervisorID = :supervisorID")
    List<Account> findClubStaffBySupervisorId(@Param("role") Role role, @Param("supervisorID") Long supervisorID);
    List<Account> findClubStaffByClub(Club club);
}
