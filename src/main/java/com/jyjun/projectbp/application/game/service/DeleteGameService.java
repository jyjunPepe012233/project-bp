package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.application.patch.service.DeletePatchService;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.permission.outbound.GameAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteGameService {

    private final LoadPatchService loadPatchService;
    private final DeletePatchService deletePatchService;
    private final GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort;
    private final GameRepositoryPort gameRepositoryPort;

    public DeleteGameService(
            LoadPatchService loadPatchService,
            DeletePatchService deletePatchService,
            GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort,
            GameRepositoryPort gameRepositoryPort
    ) {
        this.loadPatchService = loadPatchService;
        this.deletePatchService = deletePatchService;
        this.gameAccessPermissionRepositoryPort = gameAccessPermissionRepositoryPort;
        this.gameRepositoryPort = gameRepositoryPort;
    }

    public void delete(Long gameId, String gameUuid) {
        List<Patch> patches = loadPatchService.loadByGameId(gameId);
        for (Patch patch : patches) {
            deletePatchService.delete(patch, gameUuid);
        }

        gameAccessPermissionRepositoryPort.deleteByGameId(gameId);
        gameRepositoryPort.deleteById(gameId);
    }
}
