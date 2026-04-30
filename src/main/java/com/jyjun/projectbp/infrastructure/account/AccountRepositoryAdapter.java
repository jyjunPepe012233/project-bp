package com.jyjun.projectbp.infrastructure.account;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final JpaAccountRepository jpaAccountRepository;

    public AccountRepositoryAdapter(JpaAccountRepository jpaAccountRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }

    @Override
    public Account save(Account account) {
        return jpaAccountRepository.save(account);
    }

    @Override
    public Account findByName(String name) {
        return jpaAccountRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + name));
    }
}
