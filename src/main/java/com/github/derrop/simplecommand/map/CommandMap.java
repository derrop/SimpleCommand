package com.github.derrop.simplecommand.map;

import com.github.derrop.simplecommand.SubCommandPool;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.sender.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface CommandMap {

    void registerSubCommands(Object command);

    void registerCommand(@NotNull UsableCommand command);

    default void registerCommands(@NotNull UsableCommand... commands) {
        for (UsableCommand command : commands) {
            this.registerCommand(command);
        }
    }

    void unregisterCommand(@NotNull String alias);

    void unregisterCommands(@NotNull ClassLoader classLoader);

    void clearCommands();

    CommandExecutionResponse dispatchConsoleCommand(@NotNull String line);

    CommandExecutionResponse dispatchCommand(@NotNull String line, @NotNull CommandSender sender);

    @NotNull
    Collection<UsableCommand> getCommands();

    @NotNull
    Collection<String> getCommandNames();

    @Nullable
    UsableCommand getCommand(@NotNull String name);

    @Nullable
    UsableCommand parseCommand(@NotNull String line);

    @NotNull
    Collection<String> tabCompleteCommand(@NotNull String line);

}
