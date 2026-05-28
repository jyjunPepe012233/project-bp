package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.output.LoadAccountOutput;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Service;

@Service
public class LoadMyAccountUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadAccountService loadAccountService;

    public LoadMyAccountUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadAccountService loadAccountService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadAccountService = loadAccountService;
    }

    public LoadAccountOutput execute() {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Account account = loadAccountService.loadByIdOrThrow(currentAccountId);
        return new LoadAccountOutput(account.getId(), account.getName());
    }
}
