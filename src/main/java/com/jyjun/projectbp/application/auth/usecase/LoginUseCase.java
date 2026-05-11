package com.jyjun.projectbp.application.auth.usecase;

import com.jyjun.projectbp.application.auth.model.input.LoginInput;
import com.jyjun.projectbp.application.auth.model.output.LoginOutput;
import com.jyjun.projectbp.application.auth.service.IssueTokenService;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.application.auth.service.VerifyPasswordService;
import com.jyjun.projectbp.domain.account.model.Account;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final LoadAccountService loadAccountService;
    private final VerifyPasswordService verifyPasswordService;
    private final IssueTokenService issueTokenService;

    public LoginUseCase(LoadAccountService loadAccountService, VerifyPasswordService verifyPasswordService, IssueTokenService issueTokenService) {
        this.loadAccountService = loadAccountService;
        this.verifyPasswordService = verifyPasswordService;
        this.issueTokenService = issueTokenService;
    }

    @Transactional
    public LoginOutput execute(LoginInput input) {
        Account account = loadAccountService.loadByNameOrThrow(input.name());
        verifyPasswordService.verify(input.password(), account.getEncodedPassword());

        String accessToken = issueTokenService.issueAccessToken(account.getId());
        RefreshToken refreshToken = issueTokenService.issueRefreshToken(account.getId());

        return new LoginOutput(accessToken, refreshToken.getToken());
    }
}
