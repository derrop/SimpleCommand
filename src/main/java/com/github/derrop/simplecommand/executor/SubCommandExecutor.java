package com.github.derrop.simplecommand.executor;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;

import java.util.Map;

public interface SubCommandExecutor {

    void execute(CommandSender sender, String command, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties);

}
