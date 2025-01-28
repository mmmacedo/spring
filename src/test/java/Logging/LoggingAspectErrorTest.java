package Logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.domains.logging.Log;
import com.spring.domains.logging.LogEntity;
import com.spring.domains.logging.LogEntry;
import com.spring.domains.logging.LogPersistenceService;
import com.spring.domains.logging.LogPersistenceServiceImpl;
import com.spring.domains.logging.LogRepositoryFactory;
import com.spring.domains.logging.LoggingAspect;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.net.http.HttpRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EnableAspectJAutoProxy
class LoggingAspectErrorTest {

    @Autowired
    private LogPersistenceServiceImpl persistenceService;

    @Autowired
    private LoggingAspect loggingAspect;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LogRepositoryFactory logRepositoryFactory;

    @Mock
    private LogPersistenceService logPersistenceService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLogExceptionWhenMethodThrowsError() throws Throwable {
        // Mock do método anotado
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getMockedMethod());
        when(request.getRequestURI()).thenReturn("/error-endpoint");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Configuração dos nomes dos parâmetros do método
        String[] paramNames = new String[]{"param1", "param2"};
        when(methodSignature.getParameterNames()).thenReturn(paramNames);

        // Configuração dos argumentos do método
        Object[] methodArgs = new Object[]{"arg1", "arg2"};
        when(joinPoint.getArgs()).thenReturn(methodArgs);

        // Configuração para simular erro
        RuntimeException exception = new RuntimeException("Simulated exception");
        when(joinPoint.proceed()).thenThrow(exception);

        // Configuração do repositório de persistência
        when(logRepositoryFactory.getLogPersistenceService())
                .thenReturn(logPersistenceService);

        // Executa o Aspecto e espera que a exceção seja propagada
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            loggingAspect.logExecution(joinPoint, getMockedMethod().getAnnotation(Log.class));
        });

        // Valida que a exceção foi repassada corretamente
        assertEquals("Simulated exception", thrownException.getMessage());

        // Captura o log salvo
        List<LogEntity> capturedLogs = persistenceService.findAllFromTable("logs");
        assertEquals(1, capturedLogs.size());
        LogEntity capturedLog = capturedLogs.get(0);

        // Valida os dados do log
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), capturedLog.getStatusCode());
        assertEquals("127.0.0.1", capturedLog.getClientIp());
        assertNotNull(capturedLog.getRequestTime());
        assertNotNull(capturedLog.getResponseTime());
        assertTrue(capturedLog.getExecutionTimeMs() > 0);

        // Verifica se o stack trace foi salvo no responsePayload
        assertTrue(capturedLog.getResponsePayload().contains("Simulated exception"));
        assertTrue(capturedLog.getResponsePayload().contains("java.lang.RuntimeException"));
    }


    private Method getMockedMethod() throws NoSuchMethodException {
        return MockController.class.getMethod("mockedMethod");
    }

    // Mock Controller para simular o método anotado
    static class MockController {
        @Log(table = "logs")
        public String mockedMethod() {
            return "response";
        }
    }
}