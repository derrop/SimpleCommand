package com.github.derrop.simplecommand.annotation.processor;

public class ProcessedCommandUsage {

    private final String baseMessage;
    private final String permission;
    private final String description;

    public ProcessedCommandUsage(String baseMessage, String permission, String description) {
        this.baseMessage = baseMessage;
        this.permission = permission;
        this.description = description;
    }

    public String getBaseMessage() {
        return this.baseMessage;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }
}
