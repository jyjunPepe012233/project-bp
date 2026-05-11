package com.jyjun.projectbp.application.account.model.input;

public record UpdateAccountInput(
        Long accountId,
        String name
) {
}
