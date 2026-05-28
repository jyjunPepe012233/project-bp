package com.jyjun.projectbp.infrastructure.adapters.account;

import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByName(String name);

    boolean existsByName(String name);
}
