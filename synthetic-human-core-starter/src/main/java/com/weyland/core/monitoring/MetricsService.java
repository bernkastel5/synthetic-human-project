package com.weyland.core.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> authorTaskCounters = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry, ThreadPoolExecutor commandExecutor) {
        this.meterRegistry = meterRegistry;

        Gauge.builder("android.command.queue.size", commandExecutor, executor -> executor.getQueue().size())
             .description("The current number of commands waiting in the execution queue.")
             .tags("priority", "common")
             .register(meterRegistry);

        Gauge.builder("android.command.pool.active.threads", commandExecutor, ThreadPoolExecutor::getActiveCount)
             .description("The current number of threads that are actively executing tasks.")
             .register(meterRegistry);
    }

    public void incrementCompletedTasks(String author) {
        authorTaskCounters.computeIfAbsent(author, this::createAuthorCounter).increment();
    }

    private Counter createAuthorCounter(String author) {
        return Counter.builder("android.tasks.completed.total")
                      .description("Total number of completed tasks by author.")
                      .tag("author", author)
                      .register(meterRegistry);
    }
}