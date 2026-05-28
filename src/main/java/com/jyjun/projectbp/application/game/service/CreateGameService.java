package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.common.exception.DuplicateResourceException;
import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.application.version.service.CreateVersionService;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

@Component
public class CreateGameService {

    private final GameRepositoryPort gameRepositoryPort;
    private final CreateVersionService createVersionService;

    public CreateGameService(
            GameRepositoryPort gameRepositoryPort,
            CreateVersionService createVersionService
    ) {
        this.gameRepositoryPort = gameRepositoryPort;
        this.createVersionService = createVersionService;
    }

    public Game create(String title, String description, Long developerId) {
        if (gameRepositoryPort.existsByTitle(title)) {
            throw new DuplicateResourceException("같은 제목의 게임이 이미 존재합니다.");
        }
        Game game = new Game(title, description, developerId);
        Game saved = gameRepositoryPort.save(game);
        createVersionService.create(saved.getId());
        return saved;
    }
}
