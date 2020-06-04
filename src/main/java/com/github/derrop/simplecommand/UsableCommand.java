package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.executor.CommandExecutor;

public interface UsableCommand extends CommandExecutor {

    String getFirstName();

    String[] getAliases();

    String getUsage();

    String getPermission();

    String getDescription();

    String getPrefix();

}
