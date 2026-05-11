package com.jyjun.projectbp.application.account.model.input;

public record UpdateAccountPasswordInput(
        Long accountId,
        String password
) {
}
