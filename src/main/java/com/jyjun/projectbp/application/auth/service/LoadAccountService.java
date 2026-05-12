package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class LoadAccountService {

    private final AccountRepositoryPort accountRepositoryPort;

    public LoadAccountService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    public Account loadByIdOrThrow(Long id) {
        return accountRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found: id=" + id));
    }

    public Account loadByNameOrThrow(String name) {
        return accountRepositoryPort.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + name));
    }

    public List<Account> loadAllByIds(List<Long> ids) {
        return accountRepositoryPort.findAllByIds(ids);
    }
}
