package com.jyjun.projectbp.presentation.game;

import com.jyjun.projectbp.application.game.model.input.CreateGameInput;
import com.jyjun.projectbp.application.game.model.output.CreateGameOutput;
import com.jyjun.projectbp.application.game.usecase.CreateGameUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final CreateGameUseCase createGameUseCase;

    public GameController(CreateGameUseCase createGameUseCase) {
        this.createGameUseCase = createGameUseCase;
    }

    @PostMapping
    public ResponseData<CreateGameOutput> createGame(@RequestBody CreateGameInput input) {
        return new ResponseData<>(createGameUseCase.execute(input));
    }
}
