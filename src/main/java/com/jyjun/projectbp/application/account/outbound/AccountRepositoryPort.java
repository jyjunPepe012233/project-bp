package com.jyjun.projectbp.application.account.outbound;

import com.jyjun.projectbp.domain.account.model.Account;

import java.util.Optional;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(Long id);

    Optional<Account> findByName(String name);
}
