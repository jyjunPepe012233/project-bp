package com.jyjun.projectbp.presentation.developer;

import com.jyjun.projectbp.application.developer.model.input.CreateDeveloperInput;
import com.jyjun.projectbp.application.developer.model.input.UpdateDeveloperInput;
import com.jyjun.projectbp.application.developer.model.output.LoadDeveloperOutput;
import com.jyjun.projectbp.application.developer.model.output.UpdateDeveloperOutput;
import com.jyjun.projectbp.application.developer.usecase.CreateDeveloperUseCase;
import com.jyjun.projectbp.application.developer.usecase.LoadDeveloperListUseCase;
import com.jyjun.projectbp.application.developer.usecase.LoadDeveloperUseCase;
import com.jyjun.projectbp.application.developer.usecase.UpdateDeveloperUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final CreateDeveloperUseCase createDeveloperUseCase;
    private final LoadDeveloperListUseCase loadDeveloperListUseCase;
    private final LoadDeveloperUseCase loadDeveloperUseCase;
    private final UpdateDeveloperUseCase updateDeveloperUseCase;

    public DeveloperController(
            CreateDeveloperUseCase createDeveloperUseCase,
            LoadDeveloperListUseCase loadDeveloperListUseCase,
            LoadDeveloperUseCase loadDeveloperUseCase,
            UpdateDeveloperUseCase updateDeveloperUseCase
    ) {
        this.createDeveloperUseCase = createDeveloperUseCase;
        this.loadDeveloperListUseCase = loadDeveloperListUseCase;
        this.loadDeveloperUseCase = loadDeveloperUseCase;
        this.updateDeveloperUseCase = updateDeveloperUseCase;
    }

    @GetMapping
    public ResponseData<List<LoadDeveloperOutput>> loadDeveloperList() {
        return new ResponseData<>(loadDeveloperListUseCase.execute());
    }

    @PostMapping
    public void createDeveloper(@RequestBody CreateDeveloperInput input) {
        // Response나 Request DTO 쓰는 대신 Application 계층의 Input, Output 그대로 쓸거임. DTO 쓰는 곳은 다 고칠 것
        createDeveloperUseCase.execute(input);
    }

    @GetMapping("/{developerId}")
    public ResponseData<LoadDeveloperOutput> loadDeveloper(@PathVariable Long developerId) {
        return new ResponseData<>(loadDeveloperUseCase.execute(developerId));
    }

    @PatchMapping("/{developerId}")
    public ResponseData<UpdateDeveloperOutput> updateDeveloper(
            @PathVariable Long developerId,
            @RequestBody UpdateDeveloperInput input
    ) {
        return new ResponseData<>(updateDeveloperUseCase.execute(new UpdateDeveloperInput(developerId, input.name())));
    }
}
