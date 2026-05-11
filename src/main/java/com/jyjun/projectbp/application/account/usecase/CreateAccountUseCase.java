package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.service.CreateAccountService;
import com.jyjun.projectbp.domain.account.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

// 이 API 안 쓸 것 같음.
// 계정 생성은 개발자 생성하면서 루트 계정이 자동으로 생성되고, 이 루트 계정이 하위 계정을 만드는 방식으로 할 것 같음.
// 즉 아무 개발자와 연결되지 않은, 쓸모없는 계정이 존재할 수도 있으므로 독립된 계정을 생성하는 API는 안 만들 듯

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
