package com.github.derrop.simplecommand.sender;

import org.jetbrains.annotations.NotNull;

public interface CommandSender {

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull String... messages);

    boolean hasPermission(@NotNull String permission);

    String getName();

    boolean isConsole();

}
