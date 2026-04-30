package com.jyjun.projectbp.application.developer.usecase;

import com.jyjun.projectbp.application.account.service.CreateAccountService;
import com.jyjun.projectbp.application.developer.model.input.CreateDeveloperInput;
import com.jyjun.projectbp.application.developer.service.CreateDeveloperService;
import com.jyjun.projectbp.domain.account.model.Account;
import com.jyjun.projectbp.domain.developer.model.Developer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateDeveloperUseCase {

    private final CreateAccountService createAccountService;
    private final CreateDeveloperService createDeveloperService;

    public CreateDeveloperUseCase(CreateAccountService createAccountService, CreateDeveloperService createDeveloperService) {
        this.createAccountService = createAccountService;
        this.createDeveloperService = createDeveloperService;
    }

    @Transactional
    public void execute(CreateDeveloperInput input) {
        Account account = createAccountService.create(
                input.rootAccountName(),
                input.rootAccountEmail(),
                input.rootAccountPassword()
        );
        createDeveloperService.create(
                input.developerName(),
                account.getId()
        );
    }
}
