package com.jyjun.projectbp.presentation.developer;

import com.jyjun.projectbp.application.developer.model.input.CreateDeveloperInput;
import com.jyjun.projectbp.application.developer.usecase.CreateDeveloperUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final CreateDeveloperUseCase createDeveloperUseCase;

    public DeveloperController(CreateDeveloperUseCase createDeveloperUseCase) {
        this.createDeveloperUseCase = createDeveloperUseCase;
    }

    @PostMapping
    public void createDeveloper(@RequestBody CreateDeveloperInput input) {
        // Response나 Request DTO 쓰는 대신 Application 계층의 Input, Output 그대로 쓸거임. DTO 쓰는 곳은 다 고칠 것
        createDeveloperUseCase.execute(input);
    }
}
