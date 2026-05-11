package com.jyjun.projectbp.application.account.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.account.outbound.EncodePasswordPort;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountService {

    private final EncodePasswordPort encodePasswordPort;
    private final AccountRepositoryPort accountRepositoryPort;

    public CreateAccountService(EncodePasswordPort encodePasswordPort, AccountRepositoryPort accountRepositoryPort) {
        this.encodePasswordPort = encodePasswordPort;
        this.accountRepositoryPort = accountRepositoryPort;
    }

    public Account create(String name, String rawPassword) {
        String encodedPassword = encodePasswordPort.encode(rawPassword);
        Account account = new Account(name, encodedPassword);
        return accountRepositoryPort.save(account);
    }
}
