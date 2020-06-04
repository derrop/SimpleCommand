package com.github.derrop.simplecommand.executor;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.sender.CommandSender;

public interface CommandExecutor {

    void execute(UsableCommand usableCommand, CommandSender sender, String command, String[] args, String commandLine, CommandProperties properties);

}
