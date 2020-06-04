package com.github.derrop.simplecommand.executor;

import com.github.derrop.simplecommand.CommandProperties;

import java.util.Collection;

public interface TabCompleter {

    Collection<String> tabComplete(String commandLine, String[] args, CommandProperties properties);

}
