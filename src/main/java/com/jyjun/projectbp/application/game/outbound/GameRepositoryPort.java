package com.jyjun.projectbp.application.game.outbound;

import com.jyjun.projectbp.domain.game.model.Game;

public interface GameRepositoryPort {

    Game findById(Long id);
}
