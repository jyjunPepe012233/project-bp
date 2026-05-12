package com.jyjun.projectbp.application.game.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateGameInput(
        @NotNull(message = "개발사를 선택해 주세요.")
        @Positive(message = "유효하지 않은 개발사입니다.")
        Long developerId,

        @NotBlank(message = "게임 제목을 입력해 주세요.")
        @Size(max = ValidationConstants.GAME_TITLE_MAX_LENGTH, message = "게임 제목은 " + ValidationConstants.GAME_TITLE_MAX_LENGTH + "자 이하여야 합니다.")
        String title,

        @Size(max = ValidationConstants.GAME_DESC_MAX_LENGTH, message = "게임 설명은 " + ValidationConstants.GAME_DESC_MAX_LENGTH + "자 이하여야 합니다.")
        String description
) {
}
