package com.github.derrop.simplecommand.defaults;

import com.github.derrop.simplecommand.UsableCommand;

public abstract class DefaultCommand implements UsableCommand {

    private final String[] aliases;
    private final String prefix;
    private String usage;
    private String permission;
    private String description;

    public DefaultCommand(String... aliases) {
        this(aliases, null, null, null, null);
    }

    public DefaultCommand(String[] aliases, String prefix, String usage, String permission, String description) {
        if (aliases == null || aliases.length == 0) {
            throw new IllegalArgumentException("At least one alias has to be provided");
        }
        this.aliases = aliases;
        this.prefix = prefix;
        this.usage = usage;
        this.permission = permission;
        this.description = description;
    }

    protected DefaultCommand usage(String usage) {
        this.usage = usage;
        return this;
    }

    protected DefaultCommand permission(String permission) {
        this.permission = permission;
        return this;
    }

    protected DefaultCommand description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String getFirstName() {
        return this.aliases[0];
    }

    @Override
    public String[] getAliases() {
        return this.aliases;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

}
