
package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClubRepository extends JpaRepository<Club, Long> {
    Club findByClubId(Long id);
    List<Club> findClubsByAccount(Account account);
    @Query("SELECT c FROM Club c WHERE REPLACE(c.clubName, ' ', '') LIKE CONCAT('%', REPLACE(:clubName, ' ', ''), '%')")
    List<Club> findByClubNameContainingIgnoreCase(@Param("clubName") String clubName);
    @Query("SELECT c FROM Club c WHERE LOWER(c.district) LIKE LOWER(CONCAT('%', :district, '%')) AND LOWER(c.province) LIKE LOWER(CONCAT('%', :province, '%'))")
    List<Club> findByDistrictAndProvinceContainingIgnoreCase(@Param("district") String district, @Param("province") String province);
}
