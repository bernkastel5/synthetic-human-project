package com.weyland.bishop.service;

import com.weyland.core.audit.WeylandWatchingYou;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BishopInternalService {
    private static final Logger log = LoggerFactory.getLogger(BishopInternalService.class);

    @WeylandWatchingYou
    public String runDiagnostics(String initiatedBy) {
        log.info("Запускаю внутреннюю диагностику по инициативе '{}'...", initiatedBy);
        try {
            // Имитация полезной работы
            Thread.sleep(50 + (long)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Диагностика завершена.");
        return "Все системы в норме. Статус: номинальный.";
    }
}