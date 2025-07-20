package com.weyland.core.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Aspect
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditProperties auditProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditAspect(AuditProperties auditProperties, KafkaTemplate<String, String> kafkaTemplate) {
        this.auditProperties = auditProperties;
        this.kafkaTemplate = kafkaTemplate;
        log.info("Weyland-Yutani Audit Aspect is active. Mode: {}", auditProperties.mode());
    }

    @Around("@annotation(com.weyland.core.audit.WeylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }
        long executionTime = System.currentTimeMillis() - startTime;

        String auditMessage = buildAuditMessage(joinPoint, result, executionTime);

        if (auditProperties.mode() == AuditProperties.AuditMode.KAFKA) {
            sendToKafka(auditMessage);
        } else { // CONSOLE mode
            log.info("[WEYLAND AUDIT] :: {}", auditMessage);
        }

        return result;
    }

    private void sendToKafka(String message) {
        if (kafkaTemplate == null) {
            log.error("Audit mode is KAFKA, but Kafka is not configured in the application. Audit message lost: {}", message);
            return;
        }
        if (auditProperties.kafka() == null || auditProperties.kafka().topic() == null) {
            log.error("Audit mode is KAFKA, but topic is not specified in 'weyland.audit.kafka.topic'. Audit message lost: {}", message);
            return;
        }
        try {
            kafkaTemplate.send(auditProperties.kafka().topic(), message);
            log.trace("Sent audit log to Kafka topic '{}'", auditProperties.kafka().topic());
        } catch (Exception e) {
            log.error("Failed to send audit message to Kafka topic '{}'", auditProperties.kafka().topic(), e);
        }
    }

    private String buildAuditMessage(ProceedingJoinPoint joinPoint, Object result, long executionTime) {
        try {
            Map<String, Object> auditData = Map.of(
                "method", joinPoint.getSignature().toShortString(),
                "args", joinPoint.getArgs(),
                "result", result != null ? result : "void",
                "executionTimeMs", executionTime,
                "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(auditData);
        } catch (Exception e) {
            log.error("Failed to serialize audit message for method {}", joinPoint.getSignature().toShortString(), e);
            return "{\"error\":\"Failed to serialize audit data\"}";
        }
    }
}