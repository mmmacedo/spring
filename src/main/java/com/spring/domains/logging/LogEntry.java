package com.spring.domains.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {
    private LocalDateTime requestTime;
    private String requestPayload;
    private String clientIp;
    private String endpoint;
    private String httpMethod;
    private int statusCode;
    private LocalDateTime responseTime;
    private String responsePayload;
    private long executionTimeMs;

}