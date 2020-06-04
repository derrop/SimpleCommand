package com.github.derrop.simplecommand.annotation.processor;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.CommandTranslator;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.argument.CommandArgument;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.argument.ParsedArguments;
import com.github.derrop.simplecommand.executor.CommandExecutor;
import com.github.derrop.simplecommand.executor.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessedCommand implements UsableCommand, CommandExecutor, TabCompleter {

    private final Command info;
    private final Collection<ProcessedSubCommand> subCommands;

    public ProcessedCommand(Command info, Collection<ProcessedSubCommand> subCommands) {
        this.info = info;
        this.subCommands = subCommands;
    }

    @Override
    public String getFirstName() {
        return this.getAliases()[0];
    }

    @Override
    public String[] getAliases() {
        return this.info.aliases();
    }

    @Override
    public String getUsage() {
        Collection<String> messages = new ArrayList<>();
        for (ProcessedSubCommand subCommand : this.subCommands) {
            // TODO the permissions and descriptions of every sub command should be at the same width
            String message = this.getFirstName() + " " + subCommand.getArgsAsString() + subCommand.getExtendedUsage();

            if (subCommand.getPermission() != null) {
                message += " | " + subCommand.getPermission();
            }

            if (subCommand.getDescription() != null) {
                message += " | " + subCommand.getDescription();
            }
            messages.add(message);
        }
        if (messages.isEmpty()) {
            return null;
        }
        if (messages.size() == 1) {
            return messages.iterator().next();
        }
        return "\n - " + String.join("\n - ", messages);
    }

    @Override
    public String getPermission() {
        return this.info.permission().isEmpty() ? null : this.info.permission();
    }

    @Override
    public String getDescription() {
        return this.info.description().isEmpty() ? null : this.info.description();
    }

    @Override
    public String getPrefix() {
        return this.info.prefix().isEmpty() ? null : this.info.prefix();
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args, String commandLine, CommandProperties properties) {
        Optional<String> optionalInvalidMessage = this.subCommands.stream()
                .map(subCommand -> subCommand.getInvalidArgumentMessage(args))
                .filter(Objects::nonNull)
                .filter(message -> message.getNonMatchedStaticValues() == 0)
                .findFirst()
                .map(InvalidArgumentMessage::getMessage);

        Optional<ParsedArguments> optionalArguments = this.subCommands.stream()
                .map(subCommand -> new ParsedArguments(subCommand, subCommand.parseArgs(args)))
                .filter(arguments -> arguments.getArguments() != null)
                .findFirst();

        if (optionalInvalidMessage.isPresent() && !optionalArguments.isPresent()) {
            sender.sendMessage(optionalInvalidMessage.get());
            return;
        }

        if (!optionalArguments.isPresent()) {
            this.sendHelp(sender);
            return;
        }


        ParsedArguments parsedArguments = optionalArguments.get();

        ProcessedSubCommand subCommand = parsedArguments.getSubCommand();
        CommandArgument<?>[] parsedArgs = parsedArguments.getArguments();

        if (subCommand.isConsoleOnly() && !sender.isConsole()) {
            sender.sendMessage(CommandTranslator.translateMessage("only-console"));
            return;
        }

        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(CommandTranslator.translateMessage("no-permission"));
            return;
        }

        if (subCommand.isAsync()) {
            CommandTranslator.SERVICE.execute(() -> subCommand.execute(
                    sender, command, new CommandArgumentWrapper(parsedArgs),
                    commandLine, subCommand.parseProperties(args), new HashMap<>()
            ));
        } else {
            subCommand.execute(
                    sender, command, new CommandArgumentWrapper(parsedArgs),
                    commandLine, subCommand.parseProperties(args), new HashMap<>()
            );
        }
    }

    protected void sendHelp(CommandSender sender) {
        for (String usageLine : this.getUsage().split("\n")) {
            sender.sendMessage(usageLine);
        }
    }

    @Override
    public Collection<String> tabComplete(String commandLine, String[] args, CommandProperties properties) {
        return this.subCommands.stream()
                .map(subCommand -> subCommand.getNextPossibleArgumentAnswers(args))
                .filter(Objects::nonNull)
                .filter(responses -> !responses.isEmpty())
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toSet());
    }

}
