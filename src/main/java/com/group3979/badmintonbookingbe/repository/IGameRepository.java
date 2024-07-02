package com.group3979.badmintonbookingbe.repository;

import com.group3979.badmintonbookingbe.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGameRepository extends JpaRepository<Game,Long> {
}
