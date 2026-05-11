package com.jyjun.projectbp.infrastructure.game;

import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaGameRepository extends JpaRepository<Game, Long> {
}
