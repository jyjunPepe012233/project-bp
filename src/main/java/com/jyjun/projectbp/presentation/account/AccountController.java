package com.jyjun.projectbp.presentation.account;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.input.UpdateAccountInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.model.output.UpdateAccountOutput;
import com.jyjun.projectbp.application.account.usecase.CreateAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.UpdateAccountUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase, UpdateAccountUseCase updateAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
    }

    // TODO: DTO 리팩토링
    // 이 API처럼 Request/Response DTO를 따로 사용하지 않고 application의 Input/Output을 바로 사용하기.
    // 어차피 API과 UseCase가 1대1 구조이기 때문임
    @PostMapping
    public ResponseData<CreateAccountOutput> createAccount(@RequestBody CreateAccountInput input) {
        return new ResponseData<>(createAccountUseCase.execute(input));
    }

    @PatchMapping("/{accountId}")
    public ResponseData<UpdateAccountOutput> updateAccount(
            @PathVariable Long accountId,
            @RequestBody UpdateAccountInput input
    ) {
        return new ResponseData<>(updateAccountUseCase.execute(new UpdateAccountInput(accountId, input.name())));
    }
}
