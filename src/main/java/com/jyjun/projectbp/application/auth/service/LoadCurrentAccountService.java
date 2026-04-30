package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.auth.outbound.LoadCurrentAccountPort;
import org.springframework.stereotype.Component;

@Component
public class LoadCurrentAccountService {

    private final LoadCurrentAccountPort loadCurrentAccountPort;

    public LoadCurrentAccountService(LoadCurrentAccountPort loadCurrentAccountPort) {
        this.loadCurrentAccountPort = loadCurrentAccountPort;
    }

    public Long getCurrentAccountId() {
        return loadCurrentAccountPort.getCurrentAccountId();
    }
}
