package com.weyland.core.command;

import com.weyland.core.command.exception.QueueIsFullException;
import com.weyland.core.monitoring.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class CommandService {
    private static final Logger log = LoggerFactory.getLogger(CommandService.class);

    private final ThreadPoolExecutor commonPriorityExecutor;
    private final MetricsService metricsService;

    public CommandService(ThreadPoolExecutor commonPriorityExecutor, MetricsService metricsService) {
        this.commonPriorityExecutor = commonPriorityExecutor;
        this.metricsService = metricsService;
    }

    public void executeCommand(CommandDto command) {
        log.info("Received command from '{}' with priority {}", command.author(), command.priority());

        if (command.priority() == Priority.CRITICAL) {
            executeCriticalCommand(command);
        } else {
            enqueueCommonCommand(command);
        }
    }

    private void executeCriticalCommand(CommandDto command) {
        log.info("[CRITICAL COMMAND EXECUTION] Android is executing: '{}'", command.description());
        metricsService.incrementCompletedTasks(command.author());
        log.info("[CRITICAL COMMAND COMPLETED] Task from '{}' is done.", command.author());
    }

    private void enqueueCommonCommand(CommandDto command) {
        try {
            log.debug("Submitting COMMON command to the executor queue: '{}'", command.description());
            commonPriorityExecutor.execute(() -> {
                log.info("[COMMON COMMAND EXECUTION] Android started task from queue: '{}'", command.description());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Execution of common command was interrupted.", e);
                }
                metricsService.incrementCompletedTasks(command.author());
                log.info("[COMMON COMMAND COMPLETED] Task from '{}' is done.", command.author());
            });
            log.info("Command '{}' was successfully added to the COMMON queue.", command.description());
        } catch (RejectedExecutionException e) {
            log.error("Failed to add command to queue. The queue is full.", e);
            throw new QueueIsFullException("The command queue is at full capacity. Please try again later.");
        }
    }
}