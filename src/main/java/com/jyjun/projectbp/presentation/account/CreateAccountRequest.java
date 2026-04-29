package com.jyjun.projectbp.presentation.account;

public record CreateAccountRequest(
        String name,
        String email,
        String password
) {
}
