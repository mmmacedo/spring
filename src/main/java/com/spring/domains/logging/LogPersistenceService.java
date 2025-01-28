package com.spring.domains.logging;

public interface LogPersistenceService {
    void save(String tableName, LogEntry logEntry);
}
