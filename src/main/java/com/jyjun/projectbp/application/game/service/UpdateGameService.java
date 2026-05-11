package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

@Component
public class UpdateGameService {

    private final GameRepositoryPort gameRepositoryPort;
    private final LoadGameService loadGameService;

    public UpdateGameService(GameRepositoryPort gameRepositoryPort, LoadGameService loadGameService) {
        this.gameRepositoryPort = gameRepositoryPort;
        this.loadGameService = loadGameService;
    }

    public Game update(Long gameId, String title, String description) {
        Game game = loadGameService.loadByIdOrThrow(gameId);
        game.setTitle(title);
        game.setDescription(description);
        return gameRepositoryPort.save(game);
    }
}
