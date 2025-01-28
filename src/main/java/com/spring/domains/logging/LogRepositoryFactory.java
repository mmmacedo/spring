package com.spring.domains.logging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class LogRepositoryFactory {

    @Autowired
    private LogPersistenceService logPersistenceService;

}