package com.jyjun.projectbp.presentation.account;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.input.UpdateAccountInput;
import com.jyjun.projectbp.application.account.model.input.UpdateAccountPasswordInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.model.output.LoadAccountOutput;
import com.jyjun.projectbp.application.account.model.output.UpdateAccountOutput;
import com.jyjun.projectbp.application.account.usecase.CreateAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.DeleteAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.LoadAccountListUseCase;
import com.jyjun.projectbp.application.account.usecase.LoadAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.LoadMyAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.UpdateAccountUseCase;
import com.jyjun.projectbp.application.account.usecase.UpdateAccountPasswordUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final LoadAccountListUseCase loadAccountListUseCase;
    private final LoadMyAccountUseCase loadMyAccountUseCase;
    private final LoadAccountUseCase loadAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final UpdateAccountPasswordUseCase updateAccountPasswordUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;

    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            LoadAccountListUseCase loadAccountListUseCase,
            LoadMyAccountUseCase loadMyAccountUseCase,
            LoadAccountUseCase loadAccountUseCase,
            UpdateAccountUseCase updateAccountUseCase,
            UpdateAccountPasswordUseCase updateAccountPasswordUseCase,
            DeleteAccountUseCase deleteAccountUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.loadAccountListUseCase = loadAccountListUseCase;
        this.loadMyAccountUseCase = loadMyAccountUseCase;
        this.loadAccountUseCase = loadAccountUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
        this.updateAccountPasswordUseCase = updateAccountPasswordUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
    }

    @GetMapping
    public ResponseData<List<LoadAccountOutput>> loadAccountList() {
        return new ResponseData<>(loadAccountListUseCase.execute());
    }

    // TODO: DTO 리팩토링
    // 이 API처럼 Request/Response DTO를 따로 사용하지 않고 application의 Input/Output을 바로 사용하기.
    // 어차피 API과 UseCase가 1대1 구조이기 때문임
    @PostMapping
    public ResponseData<CreateAccountOutput> createAccount(@Valid @RequestBody CreateAccountInput input) {
        return new ResponseData<>(createAccountUseCase.execute(input));
    }

    @GetMapping("/me")
    public ResponseData<LoadAccountOutput> loadMyAccount() {
        return new ResponseData<>(loadMyAccountUseCase.execute());
    }

    @GetMapping("/{accountId}")
    public ResponseData<LoadAccountOutput> loadAccount(@PathVariable Long accountId) {
        return new ResponseData<>(loadAccountUseCase.execute(accountId));
    }

    @PatchMapping("/{accountId}")
    public ResponseData<UpdateAccountOutput> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountInput input
    ) {
        return new ResponseData<>(updateAccountUseCase.execute(new UpdateAccountInput(accountId, input.name())));
    }

    @PatchMapping("/{accountId}/password")
    public void updateAccountPassword(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountPasswordInput input
    ) {
        updateAccountPasswordUseCase.execute(new UpdateAccountPasswordInput(accountId, input.password()));
    }

    @DeleteMapping("/{accountId}")
    public void deleteAccount(@PathVariable Long accountId) {
        deleteAccountUseCase.execute(accountId);
    }
}
