package com.jyjun.projectbp.infrastructure.adapters.game;

import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaGameRepository extends JpaRepository<Game, Long> {

    List<Game> findByDeveloperId(Long developerId);

    boolean existsByTitle(String title);
}
