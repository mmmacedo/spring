package com.spring.domains.logging;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LogRepositoryFactory {

    @Autowired
    private LogPersistenceService logPersistenceService;


    public LogPersistenceService getLogPersistenceService(String tableName) {
        return logPersistenceService;
    }
}