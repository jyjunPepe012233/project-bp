package com.jyjun.projectbp.application.auth.outbound;

public interface IssueAccessTokenPort {

    String issue(Long accountId);
}
