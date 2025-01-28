package com.spring.domains.logging;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter
public class LogPersistenceServiceImpl implements LogPersistenceService {

    @Autowired
    private LogRepository logRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(String tableName, LogEntry logEntry) {
        String sql = "INSERT INTO " + tableName +
                " (request_time, request_payload, client_ip, endpoint, http_method, status_code, response_time, response_payload, execution_time_ms) " +
                "VALUES (:requestTime, :requestPayload, :clientIp, :endpoint, :httpMethod, :statusCode, :responseTime, :responsePayload, :executionTimeMs)";
        try {
            entityManager.createNativeQuery(sql)
                    .setParameter("requestTime", logEntry.getRequestTime())
                    .setParameter("requestPayload", logEntry.getRequestPayload())
                    .setParameter("clientIp", logEntry.getClientIp())
                    .setParameter("endpoint", logEntry.getEndpoint())
                    .setParameter("httpMethod", logEntry.getHttpMethod())
                    .setParameter("statusCode", logEntry.getStatusCode())
                    .setParameter("responseTime", logEntry.getResponseTime())
                    .setParameter("responsePayload", logEntry.getResponsePayload())
                    .setParameter("executionTimeMs", logEntry.getExecutionTimeMs())
                    .executeUpdate();
        } catch (TransactionRequiredException e) {
            System.out.println(e.getMessage());
        }
    }
}