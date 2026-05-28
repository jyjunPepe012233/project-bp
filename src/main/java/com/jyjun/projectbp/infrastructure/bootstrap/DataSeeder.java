package com.jyjun.projectbp.infrastructure.bootstrap;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.account.service.CreateAccountService;
import com.jyjun.projectbp.application.developer.service.CreateDeveloperService;
import com.jyjun.projectbp.application.game.service.CreateGameService;
import com.jyjun.projectbp.application.patch.service.CreatePatchService;
import com.jyjun.projectbp.application.permission.service.CreateDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.CreateGameAccessPermissionService;
import com.jyjun.projectbp.domain.account.model.Account;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import com.jyjun.projectbp.domain.patch.model.Patch;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String ROOT_ACCOUNT_NAME = "seed_root";
    private static final String ROOT_ACCOUNT_PASSWORD = "seed_root_1234";
    private static final String DEVELOPER_NAME = "Seed Developer";
    private static final String GAME_TITLE = "Seed Game";
    private static final String GAME_DESCRIPTION = "Test seed data";

    private final AccountRepositoryPort accountRepositoryPort;
    private final CreateAccountService createAccountService;
    private final CreateDeveloperService createDeveloperService;
    private final CreateGameService createGameService;
    private final CreatePatchService createPatchService;
    private final CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService;
    private final CreateGameAccessPermissionService createGameAccessPermissionService;

    public DataSeeder(
            AccountRepositoryPort accountRepositoryPort,
            CreateAccountService createAccountService,
            CreateDeveloperService createDeveloperService,
            CreateGameService createGameService,
            CreatePatchService createPatchService,
            CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService,
            CreateGameAccessPermissionService createGameAccessPermissionService
    ) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.createAccountService = createAccountService;
        this.createDeveloperService = createDeveloperService;
        this.createGameService = createGameService;
        this.createPatchService = createPatchService;
        this.createDeveloperAccessPermissionService = createDeveloperAccessPermissionService;
        this.createGameAccessPermissionService = createGameAccessPermissionService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (accountRepositoryPort.existsByName(ROOT_ACCOUNT_NAME)) {
            log.info("테스트 데이터인 Root Account가 '{}'가 이미 존재함", ROOT_ACCOUNT_NAME);
            return;
        }

        Account root = createAccountService.create(ROOT_ACCOUNT_NAME, ROOT_ACCOUNT_PASSWORD);
        Developer developer = createDeveloperService.create(DEVELOPER_NAME, root.getId());
        Game game = createGameService.create(GAME_TITLE, GAME_DESCRIPTION, developer.getId());

        createDeveloperAccessPermissionService.create(root.getId(), developer.getId(), DeveloperAccessPermissionType.ADMIN);
        createGameAccessPermissionService.create(root.getId(), game.getId(), GameAccessPermissionType.ADMIN);

        Patch androidPatch = createPatchService.create(
                game.getId(),
                "1.0.0",
                PatchPlatform.ANDROID,
                "Initial Android patch from seed data."
        );
        Patch iosPatch = createPatchService.create(
                game.getId(),
                "1.0.0",
                PatchPlatform.IOS,
                "Initial iOS patch from seed data."
        );

        log.info(
                "테스트 데이터: account={}, developerId={}, gameId={}, androidPatchId={}, iosPatchId={}",
                root.getName(),
                developer.getId(),
                game.getId(),
                androidPatch.getId(),
                iosPatch.getId()
        );
    }
}
