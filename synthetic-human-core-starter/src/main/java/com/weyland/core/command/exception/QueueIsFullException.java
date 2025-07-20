package com.weyland.core.command.exception;

/**
 * Исключение, выбрасываемое при попытке добавить команду в переполненную очередь.
 */
public class QueueIsFullException extends RuntimeException {
    public QueueIsFullException(String message) {
        super(message);
    }
}