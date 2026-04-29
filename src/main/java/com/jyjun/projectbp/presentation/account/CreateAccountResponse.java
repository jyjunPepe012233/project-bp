package com.jyjun.projectbp.presentation.account;

import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;

public record CreateAccountResponse(
        String name,
        String email
) {
    public CreateAccountResponse(CreateAccountOutput output) {
        this(output.name(), output.email());
    }
}
