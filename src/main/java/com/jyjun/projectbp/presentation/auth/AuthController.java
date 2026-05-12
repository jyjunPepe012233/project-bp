package com.jyjun.projectbp.presentation.auth;

import com.jyjun.projectbp.application.auth.model.input.LoginInput;
import com.jyjun.projectbp.application.auth.model.input.ReissueAccessTokenInput;
import com.jyjun.projectbp.application.auth.model.output.LoginOutput;
import com.jyjun.projectbp.application.auth.model.output.ReissueAccessTokenOutput;
import com.jyjun.projectbp.application.auth.usecase.LoginUseCase;
import com.jyjun.projectbp.application.auth.usecase.ReissueAccessTokenUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ReissueAccessTokenUseCase reissueAccessTokenUseCase;

    public AuthController(LoginUseCase loginUseCase, ReissueAccessTokenUseCase reissueAccessTokenUseCase) {
        this.loginUseCase = loginUseCase;
        this.reissueAccessTokenUseCase = reissueAccessTokenUseCase;
    }

    @PostMapping("/login")
    public ResponseData<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginOutput output = loginUseCase.execute(new LoginInput(request.name(), request.password()));
        return new ResponseData<>(new LoginResponse(output));
    }

    @PostMapping("/reissue")
    public ResponseData<ReissueAccessTokenResponse> reissue(@Valid @RequestBody ReissueAccessTokenRequest request) {
        ReissueAccessTokenOutput output = reissueAccessTokenUseCase.execute(
                new ReissueAccessTokenInput(request.refreshToken())
        );
        return new ResponseData<>(new ReissueAccessTokenResponse(output));
    }
}
