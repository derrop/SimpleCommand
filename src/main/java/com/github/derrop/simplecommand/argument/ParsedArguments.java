package com.github.derrop.simplecommand.argument;

import com.github.derrop.simplecommand.annotation.processor.ProcessedSubCommand;

public class ParsedArguments {

    private final ProcessedSubCommand subCommand;
    private final CommandArgument<?>[] arguments;

    public ParsedArguments(ProcessedSubCommand subCommand, CommandArgument<?>[] arguments) {
        this.subCommand = subCommand;
        this.arguments = arguments;
    }

    public ProcessedSubCommand getSubCommand() {
        return this.subCommand;
    }

    public CommandArgument<?>[] getArguments() {
        return this.arguments;
    }
}
