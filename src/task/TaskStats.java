package task;

public record TaskStats (
    int total,
    int highPriority,
    int mediumPriority,
    int lowPriority,
    int completed,
    int pending
) {}
