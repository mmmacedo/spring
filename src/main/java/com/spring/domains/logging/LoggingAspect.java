package com.spring.domains.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private LogRepositoryFactory logRepositoryFactory;
    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(logAnnotation)")
    public Object logExecution(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        // Captura dinamicamente o HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        LocalDateTime requestTime = LocalDateTime.now();
        String requestBody = getRequestPayload(joinPoint);
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        Object response = null;
        String responseBody = null;
        LocalDateTime responseTime;
        int statusCode = 200;

        try {
            response = joinPoint.proceed(); // Executa o método original
            responseBody = objectMapper.writeValueAsString(response);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            responseBody = sw.toString();
            statusCode = 500;
            throw ex;
        } finally {
            responseTime = LocalDateTime.now();
            long executionTime = responseTime.toInstant(ZoneOffset.UTC).toEpochMilli() - requestTime.toInstant(ZoneOffset.UTC).toEpochMilli();

            // Monta o log
            LogEntry logEntry = LogEntry.builder()
                    .requestTime(requestTime)
                    .requestPayload(requestBody)
                    .clientIp(clientIp)
                    .endpoint(endpoint)
                    .httpMethod(method)
                    .statusCode(statusCode)
                    .responseTime(responseTime)
                    .responsePayload(responseBody)
                    .executionTimeMs(executionTime)
                    .build();

            // Salva o log na tabela correta
            String tableName = logAnnotation.table();
            LogPersistenceService persistenceService = logRepositoryFactory.getLogPersistenceService(tableName);
            persistenceService.save(tableName, logEntry);
        }
        return response;
    }

    private String getRequestPayload(ProceedingJoinPoint joinPoint) throws Exception {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> payload = new HashMap<>();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < args.length; i++) {
            payload.put(paramNames[i], args[i]);
        }
        return objectMapper.writeValueAsString(payload);
    }

    private String getLogTableName(ProceedingJoinPoint joinPoint, Log logAnnotation) {
        return logAnnotation.table();
    }
}