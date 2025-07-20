package com.weyland.core.audit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "weyland.audit")
@Validated
public record AuditProperties(
    @NotNull(message = "Audit mode must be specified (CONSOLE or KAFKA)")
    AuditMode mode,
    Kafka kafka
) {
    public enum AuditMode {
        CONSOLE,
        KAFKA
    }

    public record Kafka(String topic) {}
}