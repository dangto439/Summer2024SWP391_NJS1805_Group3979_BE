package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.ImageClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IImageClubRespository extends JpaRepository<ImageClub, Long> {
    List<ImageClub> findByClub(Club club);
    ImageClub findById(long id);
}
