package com.jyjun.projectbp.infrastructure.account;

import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAccountRepository extends JpaRepository<Account, Long> {
}
