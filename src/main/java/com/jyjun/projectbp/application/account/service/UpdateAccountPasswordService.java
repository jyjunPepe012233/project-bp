package com.jyjun.projectbp.application.account.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.account.outbound.EncodePasswordPort;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Component;

@Component
public class UpdateAccountPasswordService {

    private final EncodePasswordPort encodePasswordPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final LoadAccountService loadAccountService;

    public UpdateAccountPasswordService(EncodePasswordPort encodePasswordPort, AccountRepositoryPort accountRepositoryPort, LoadAccountService loadAccountService) {
        this.encodePasswordPort = encodePasswordPort;
        this.accountRepositoryPort = accountRepositoryPort;
        this.loadAccountService = loadAccountService;
    }

    public void updatePassword(Long accountId, String rawPassword) {
        Account account = loadAccountService.loadByIdOrThrow(accountId);
        account.setEncodedPassword(encodePasswordPort.encode(rawPassword));
        accountRepositoryPort.save(account);
    }
}
