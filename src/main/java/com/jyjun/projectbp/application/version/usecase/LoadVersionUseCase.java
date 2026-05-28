package com.jyjun.projectbp.application.version.usecase;

import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.version.model.output.LoadVersionOutput;
import com.jyjun.projectbp.application.version.service.LoadVersionService;
import org.springframework.stereotype.Service;

@Service
public class LoadVersionUseCase {

    private final LoadGameService loadGameService;
    private final LoadVersionService loadVersionService;

    public LoadVersionUseCase(
            LoadGameService loadGameService,
            LoadVersionService loadVersionService
    ) {
        this.loadGameService = loadGameService;
        this.loadVersionService = loadVersionService;
    }

    public LoadVersionOutput execute(Long gameId) {
        loadGameService.loadByIdOrThrow(gameId);
        return loadVersionService.loadResolvedByGameIdOrThrow(gameId);
    }
}
