package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class LoadGameService {

    private final GameRepositoryPort gameRepositoryPort;

    public LoadGameService(GameRepositoryPort gameRepositoryPort) {
        this.gameRepositoryPort = gameRepositoryPort;
    }

    public Game loadByIdOrThrow(Long id) {
        return gameRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Game not found: id=" + id));
    }
}
