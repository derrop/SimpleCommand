package com.github.derrop.simplecommand.sender;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class DefaultCollectionCommandSender implements CommandSender {

    private final Collection<String> messages;

    public DefaultCollectionCommandSender(Collection<String> messages) {
        this.messages = messages;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.messages.add(message);
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        this.messages.addAll(Arrays.asList(messages));
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public String getName() {
        return "CollectionCommandSender";
    }

    @Override
    public boolean isConsole() {
        return false;
    }
}
