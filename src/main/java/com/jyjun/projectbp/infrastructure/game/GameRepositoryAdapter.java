package com.jyjun.projectbp.infrastructure.game;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GameRepositoryAdapter implements GameRepositoryPort {

    private final JpaGameRepository jpaGameRepository;

    public GameRepositoryAdapter(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public Game save(Game game) {
        return jpaGameRepository.save(game);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return jpaGameRepository.findById(id);
    }

    @Override
    public List<Game> findByDeveloperId(Long developerId) {
        return jpaGameRepository.findByDeveloperId(developerId);
    }

    @Override
    public List<Game> findAllByIds(List<Long> ids) {
        return jpaGameRepository.findAllById(ids);
    }

    @Override
    public boolean existsByTitle(String title) {
        return jpaGameRepository.existsByTitle(title);
    }

    @Override
    public void deleteById(Long id) {
        jpaGameRepository.deleteById(id);
    }
}
