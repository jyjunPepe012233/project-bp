package com.jyjun.projectbp.application.game.outbound;

import com.jyjun.projectbp.domain.game.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepositoryPort {

    Game save(Game game);

    Optional<Game> findById(Long id);

    List<Game> findByDeveloperId(Long developerId);

    List<Game> findAllByIds(List<Long> ids);

    boolean existsByTitle(String title);

    void deleteById(Long id);
}
