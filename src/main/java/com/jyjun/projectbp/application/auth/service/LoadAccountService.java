package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Component;

@Component
public class LoadAccountService {

    private final AccountRepositoryPort accountRepositoryPort;

    public LoadAccountService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    public Account loadByName(String name) {
        return accountRepositoryPort.findByName(name);
    }
}
