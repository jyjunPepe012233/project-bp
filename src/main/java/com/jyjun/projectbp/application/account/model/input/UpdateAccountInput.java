package com.jyjun.projectbp.application.account.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.Pattern;

public record UpdateAccountInput(
        Long accountId,

        @Pattern(regexp = ValidationConstants.ACCOUNT_NAME_PATTERN, message = ValidationConstants.ACCOUNT_NAME_MESSAGE)
        String name
) {
}
