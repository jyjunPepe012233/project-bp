package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

@Component
public class LoadGameService {

    private final GameRepositoryPort gameRepositoryPort;

    public LoadGameService(GameRepositoryPort gameRepositoryPort) {
        this.gameRepositoryPort = gameRepositoryPort;
    }

    public Game loadById(Long id) {
        return gameRepositoryPort.findById(id);
    }
}
