package com.weyland.core.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommandDto(
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description must be up to 1000 characters")
    String description,

    @NotNull(message = "Priority is required")
    Priority priority,

    @NotBlank(message = "Author cannot be blank")
    @Size(max = 100, message = "Author must be up to 100 characters")
    String author,

    @NotBlank(message = "Time cannot be blank")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?(?:Z|[+-]\\d{2}:\\d{2})$",
        message = "Time must be in ISO-8601 format (e.g., '2024-05-21T10:00:00Z')"
    )
    String time
) {
}