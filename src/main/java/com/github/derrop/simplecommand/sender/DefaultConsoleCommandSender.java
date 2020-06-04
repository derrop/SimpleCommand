package com.github.derrop.simplecommand.sender;

import org.jetbrains.annotations.NotNull;

public class DefaultConsoleCommandSender implements CommandSender {

    private static final String CONSOLE_NAME = "Console";

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(message);
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public String getName() {
        return CONSOLE_NAME;
    }

    @Override
    public boolean isConsole() {
        return true;
    }
}
