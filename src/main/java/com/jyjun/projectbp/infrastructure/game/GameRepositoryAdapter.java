package com.jyjun.projectbp.infrastructure.game;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
public class GameRepositoryAdapter implements GameRepositoryPort {

    private final JpaGameRepository jpaGameRepository;

    public GameRepositoryAdapter(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public Game findById(Long id) {
        return jpaGameRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Game not found: id=" + id));
    }
}
