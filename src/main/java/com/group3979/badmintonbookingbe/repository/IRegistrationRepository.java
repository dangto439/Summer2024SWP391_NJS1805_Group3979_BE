package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Contest;
import com.group3979.badmintonbookingbe.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRegistrationRepository extends JpaRepository<Registration,Long> {
    List<Registration> findRegistrationByContest(Contest contest);
}
