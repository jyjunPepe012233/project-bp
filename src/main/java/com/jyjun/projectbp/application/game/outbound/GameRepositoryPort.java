package com.jyjun.projectbp.application.game.outbound;

import com.jyjun.projectbp.domain.game.model.Game;

import java.util.Optional;

public interface GameRepositoryPort {

    Game save(Game game);

    Optional<Game> findById(Long id);
}
