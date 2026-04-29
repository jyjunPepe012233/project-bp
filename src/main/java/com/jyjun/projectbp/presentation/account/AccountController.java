package com.jyjun.projectbp.presentation.account;

import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.usecase.CreateAccountUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountOutput output = createAccountUseCase.execute(
                new CreateAccountInput(request.name(), request.email(), request.password())
        );
        return ResponseEntity.ok(new CreateAccountResponse(output));
    }
}
