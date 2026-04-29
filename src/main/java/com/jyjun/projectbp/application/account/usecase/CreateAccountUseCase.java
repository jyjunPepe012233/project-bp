package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.account.outbound.EncodePasswordPort;
import com.jyjun.projectbp.domain.account.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final EncodePasswordPort encodePasswordPort;

    public CreateAccountUseCase(AccountRepositoryPort accountRepositoryPort, EncodePasswordPort encodePasswordPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.encodePasswordPort = encodePasswordPort;
    }

    @Transactional
    public void execute(CreateAccountInput input) {

        String encodedPassword = encodePasswordPort.encode(input.password());

        Account account = new Account(
                input.name(),
                input.email(),
                encodedPassword
        );
        Account saved = accountRepositoryPort.save(account);
    }
}
