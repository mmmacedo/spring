package Logging;

import com.spring.MainApplication;
import com.spring.domains.logging.LogEntity;
import com.spring.domains.logging.LogEntry;
import com.spring.domains.logging.LogPersistenceServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EnableAspectJAutoProxy
public class PersistenceServiceErrorInIT {

    @Autowired
    private LogPersistenceServiceImpl persistenceService;

    @Test
    @Transactional
    void shouldSaveLogEntrySuccessfully() {
        // Configura um LogEntry de exemplo
        LogEntry logEntry = new LogEntry();
        logEntry.setRequestTime(LocalDateTime.now());
        logEntry.setRequestPayload("{\"param\":\"value\"}");
        logEntry.setClientIp("127.0.0.1");
        logEntry.setEndpoint("/example-endpoint");
        logEntry.setHttpMethod("POST");
        logEntry.setStatusCode(200);
        logEntry.setResponseTime(LocalDateTime.now());
        logEntry.setResponsePayload("{\"response\":\"ok\"}");
        logEntry.setExecutionTimeMs(120L);

        // Salva o log no banco
        persistenceService.save("logs", logEntry);

        List<LogEntity> savedLogs = persistenceService.findAllFromTable("logs");
        assertEquals(1, savedLogs.size());

        var savedLog = savedLogs.get(0);
        assertNotNull(savedLog.getId());
        assertEquals("127.0.0.1", savedLog.getClientIp());
        assertEquals("/example-endpoint", savedLog.getEndpoint());
        assertEquals("POST", savedLog.getHttpMethod());
        assertEquals(200, savedLog.getStatusCode());
        assertEquals("{\"response\":\"ok\"}", savedLog.getResponsePayload());
    }

    @Test
    void shouldThrowExceptionWhenDatabaseErrorOccurs() {
        // LogEntry incompleto (simulando erro)
        LogEntry logEntry = new LogEntry();
        logEntry.setRequestPayload(null); // Campo obrigatório ausente

        Exception exception = null;

        try {
            persistenceService.save("logs", logEntry);
        } catch (Exception ex) {
            exception = ex;
        }

        assertNotNull(exception);
    }
}