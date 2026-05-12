package com.jyjun.projectbp.presentation.game;

import com.jyjun.projectbp.application.game.model.input.CreateGameInput;
import com.jyjun.projectbp.application.game.model.input.UpdateGameInput;
import com.jyjun.projectbp.application.game.model.output.CreateGameOutput;
import com.jyjun.projectbp.application.game.model.output.LoadGameOutput;
import com.jyjun.projectbp.application.game.model.output.UpdateGameOutput;
import com.jyjun.projectbp.application.game.usecase.CreateGameUseCase;
import com.jyjun.projectbp.application.game.usecase.LoadGameUseCase;
import com.jyjun.projectbp.application.game.usecase.UpdateGameUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final CreateGameUseCase createGameUseCase;
    private final LoadGameUseCase loadGameUseCase;
    private final UpdateGameUseCase updateGameUseCase;

    public GameController(CreateGameUseCase createGameUseCase, LoadGameUseCase loadGameUseCase, UpdateGameUseCase updateGameUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.loadGameUseCase = loadGameUseCase;
        this.updateGameUseCase = updateGameUseCase;
    }

    @PostMapping
    public ResponseData<CreateGameOutput> createGame(@RequestBody CreateGameInput input) {
        return new ResponseData<>(createGameUseCase.execute(input));
    }

    @GetMapping("/{gameId}")
    public ResponseData<LoadGameOutput> loadGame(@PathVariable Long gameId) {
        return new ResponseData<>(loadGameUseCase.execute(gameId));
    }

    @PatchMapping("/{gameId}")
    public ResponseData<UpdateGameOutput> updateGame(
            @PathVariable Long gameId,
            @RequestBody UpdateGameInput input
    ) {
        return new ResponseData<>(updateGameUseCase.execute(new UpdateGameInput(gameId, input.title(), input.description())));
    }
}
