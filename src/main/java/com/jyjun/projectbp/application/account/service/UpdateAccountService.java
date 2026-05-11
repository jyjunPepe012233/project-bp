package com.jyjun.projectbp.application.account.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Component;

@Component
public class UpdateAccountService {

    private final AccountRepositoryPort accountRepositoryPort;
    private final LoadAccountService loadAccountService;

    public UpdateAccountService(AccountRepositoryPort accountRepositoryPort, LoadAccountService loadAccountService) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.loadAccountService = loadAccountService;
    }

    public Account updateName(Long accountId, String name) {
        Account account = loadAccountService.loadByIdOrThrow(accountId);
        account.setName(name);
        return accountRepositoryPort.save(account);
    }
}
