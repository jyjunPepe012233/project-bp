package com.jyjun.projectbp.infrastructure.account;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Account> findById(Long id) {
        return jpaAccountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByName(String name) {
        return jpaAccountRepository.findByName(name);
    }

    @Override
    public List<Account> findAllByIds(List<Long> ids) {
        return jpaAccountRepository.findAllById(ids);
    }
}
