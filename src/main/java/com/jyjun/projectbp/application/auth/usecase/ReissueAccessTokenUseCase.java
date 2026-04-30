package com.jyjun.projectbp.application.auth.usecase;

import com.jyjun.projectbp.application.auth.model.input.ReissueAccessTokenInput;
import com.jyjun.projectbp.application.auth.model.output.ReissueAccessTokenOutput;
import com.jyjun.projectbp.application.auth.service.IssueTokenService;
import com.jyjun.projectbp.application.auth.service.ValidateRefreshTokenService;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReissueAccessTokenUseCase {

    private final ValidateRefreshTokenService validateRefreshTokenService;
    private final IssueTokenService issueTokenService;

    public ReissueAccessTokenUseCase(ValidateRefreshTokenService validateRefreshTokenService, IssueTokenService issueTokenService) {
        this.validateRefreshTokenService = validateRefreshTokenService;
        this.issueTokenService = issueTokenService;
    }

    @Transactional
    public ReissueAccessTokenOutput execute(ReissueAccessTokenInput input) {
        Long accountId = validateRefreshTokenService.validate(input.refreshToken());

        String accessToken = issueTokenService.issueAccessToken(accountId);
        RefreshToken newRefreshToken = issueTokenService.issueRefreshToken(accountId);

        return new ReissueAccessTokenOutput(accessToken, newRefreshToken.getToken());
    }
}
