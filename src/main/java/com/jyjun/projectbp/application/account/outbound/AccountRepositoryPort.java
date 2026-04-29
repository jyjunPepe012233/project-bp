package com.jyjun.projectbp.application.account.outbound;

import com.jyjun.projectbp.domain.account.model.Account;

public interface AccountRepositoryPort {

    Account save(Account account);
}
