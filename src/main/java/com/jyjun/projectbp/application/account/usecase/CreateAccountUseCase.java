package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.service.CreateAccountService;
import com.jyjun.projectbp.domain.account.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountUseCase {

    private final CreateAccountService createAccountService;

    public CreateAccountUseCase(CreateAccountService createAccountService) {
        this.createAccountService = createAccountService;
    }

    @Transactional
    public CreateAccountOutput execute(CreateAccountInput input) {
        Account created = createAccountService.create(
                input.name(),
                input.email(),
                input.password()
        );
        return new CreateAccountOutput(created.getName(), created.getEmail());
    }
}
