package com.github.derrop.simplecommand.map;

import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.sender.CommandSender;

public class CommandExecutionException extends RuntimeException {

    private final UsableCommand command;
    private final CommandSender sender;

    public CommandExecutionException(UsableCommand command, CommandSender sender, String line, Throwable cause) {
        super(String.format("An error occurred while attempting to perform command '%s' as '%s'", line, sender.getName()), cause);
        this.command = command;
        this.sender = sender;
    }

    public UsableCommand getCommand() {
        return this.command;
    }

    public CommandSender getSender() {
        return this.sender;
    }
}
