
package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClubRepository extends JpaRepository<Club, Long> {
    Club findByClubId(Long id);
    List<Club> findClubsByAccount(Account account);
}
