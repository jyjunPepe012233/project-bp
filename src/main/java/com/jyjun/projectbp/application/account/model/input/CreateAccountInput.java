package com.jyjun.projectbp.application.account.model.input;

public record CreateAccountInput(
        String name,
        String email,
        String password
) {
}
