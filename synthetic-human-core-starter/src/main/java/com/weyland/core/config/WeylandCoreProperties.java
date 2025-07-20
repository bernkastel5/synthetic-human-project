package com.weyland.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.validation.constraints.Min;

@ConfigurationProperties(prefix = "weyland.core")
public record WeylandCoreProperties(
    Queue queue
) {
    public record Queue(
        @Min(1) int corePoolSize,
        @Min(1) int maxPoolSize,
        @Min(1) int capacity
    ) {}
}