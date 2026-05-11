package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

@Component
public class CreateGameService {

    private final GameRepositoryPort gameRepositoryPort;

    public CreateGameService(GameRepositoryPort gameRepositoryPort) {
        this.gameRepositoryPort = gameRepositoryPort;
    }

    public Game create(String title, String description, Long developerId) {
        Game game = new Game(title, description, developerId);
        return gameRepositoryPort.save(game);
    }
}
