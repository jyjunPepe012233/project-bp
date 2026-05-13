package com.jyjun.projectbp.application.account.service;

import com.jyjun.projectbp.common.exception.DuplicateResourceException;
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
        if (accountRepositoryPort.existsByName(name)) {
            throw new DuplicateResourceException("같은 이름의 계정이 이미 존재합니다.");
        }
        String encodedPassword = encodePasswordPort.encode(rawPassword);
        Account account = new Account(name, encodedPassword);
        return accountRepositoryPort.save(account);
    }
}
