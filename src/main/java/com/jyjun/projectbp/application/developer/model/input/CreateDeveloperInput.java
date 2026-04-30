package com.jyjun.projectbp.application.developer.model.input;

public record CreateDeveloperInput(
        String developerName,
        String rootAccountName,
        String rootAccountEmail,
        String rootAccountPassword
) {
}
