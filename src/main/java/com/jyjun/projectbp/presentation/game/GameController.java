package com.jyjun.projectbp.presentation.game;

import com.jyjun.projectbp.application.game.model.input.CreateGameInput;
import com.jyjun.projectbp.application.game.model.input.UpdateGameInput;
import com.jyjun.projectbp.application.game.model.output.CreateGameOutput;
import com.jyjun.projectbp.application.game.model.output.LoadGameOutput;
import com.jyjun.projectbp.application.game.model.output.UpdateGameOutput;
import com.jyjun.projectbp.application.game.usecase.CreateGameUseCase;
import com.jyjun.projectbp.application.game.usecase.DeleteGameUseCase;
import com.jyjun.projectbp.application.game.usecase.LoadGameListUseCase;
import com.jyjun.projectbp.application.game.usecase.LoadGameUseCase;
import com.jyjun.projectbp.application.game.usecase.UpdateGameUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final CreateGameUseCase createGameUseCase;
    private final LoadGameListUseCase loadGameListUseCase;
    private final LoadGameUseCase loadGameUseCase;
    private final UpdateGameUseCase updateGameUseCase;
    private final DeleteGameUseCase deleteGameUseCase;

    public GameController(
            CreateGameUseCase createGameUseCase,
            LoadGameListUseCase loadGameListUseCase,
            LoadGameUseCase loadGameUseCase,
            UpdateGameUseCase updateGameUseCase,
            DeleteGameUseCase deleteGameUseCase
    ) {
        this.createGameUseCase = createGameUseCase;
        this.loadGameListUseCase = loadGameListUseCase;
        this.loadGameUseCase = loadGameUseCase;
        this.updateGameUseCase = updateGameUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    @GetMapping
    public ResponseData<List<LoadGameOutput>> loadGameList() {
        return new ResponseData<>(loadGameListUseCase.execute());
    }

    @PostMapping
    public ResponseData<CreateGameOutput> createGame(@Valid @RequestBody CreateGameInput input) {
        return new ResponseData<>(createGameUseCase.execute(input));
    }

    @GetMapping("/{gameId}")
    public ResponseData<LoadGameOutput> loadGame(@PathVariable Long gameId) {
        return new ResponseData<>(loadGameUseCase.execute(gameId));
    }

    @PatchMapping("/{gameId}")
    public ResponseData<UpdateGameOutput> updateGame(
            @PathVariable Long gameId,
            @Valid @RequestBody UpdateGameInput input
    ) {
        return new ResponseData<>(updateGameUseCase.execute(new UpdateGameInput(gameId, input.title(), input.description())));
    }

    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable Long gameId) {
        deleteGameUseCase.execute(gameId);
    }
}
