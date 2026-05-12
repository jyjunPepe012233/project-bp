package com.jyjun.projectbp.application.developer.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.Pattern;

public record UpdateDeveloperInput(
        Long developerId,

        @Pattern(regexp = ValidationConstants.DISPLAY_NAME_PATTERN, message = ValidationConstants.DISPLAY_NAME_MESSAGE)
        String name
) {
}
