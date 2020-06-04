package com.github.derrop.simplecommand.map;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.annotation.processor.AnnotationProcessor;
import com.github.derrop.simplecommand.defaults.DefaultHelpCommand;
import com.github.derrop.simplecommand.executor.TabCompleter;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.sender.DefaultConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultCommandMap implements CommandMap {

    private final Map<String, UsableCommand> commands = new HashMap<>();
    private final CommandSender consoleSender = new DefaultConsoleCommandSender();

    @Override
    public void registerDefaultHelpCommand() {
        this.registerSubCommands(new DefaultHelpCommand(this));
    }

    @Override
    public UsableCommand registerSubCommands(@NotNull Object command) {
        try {
            UsableCommand usableCommand = AnnotationProcessor.process(command);
            if (usableCommand != null) {
                this.registerCommand(usableCommand);
            }
            return usableCommand;
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void registerCommand(@NotNull UsableCommand command) {
        for (String alias : command.getAliases()) {
            alias = alias.toLowerCase();
            this.commands.put(alias, command);

            if (command.getPrefix() != null && !command.getPrefix().isEmpty()) {
                this.commands.put(command.getPrefix().toLowerCase() + ":" + alias, command);
            }
        }
    }

    @Override
    public void unregisterCommand(@NotNull String alias) {
        this.commands.remove(alias.toLowerCase());
    }

    @Override
    public void unregisterCommands(@NotNull ClassLoader classLoader) {
        this.commands.values().removeIf(command -> command.getClass().getClassLoader().equals(classLoader));
    }

    @Override
    public void clearCommands() {
        this.commands.clear();
    }

    @Override
    public CommandExecutionResponse dispatchConsoleCommand(@NotNull String line) {
        return this.dispatchCommand(line, this.consoleSender);
    }

    @Override
    public CommandExecutionResponse dispatchCommand(@NotNull String line, @NotNull CommandSender sender) {
        String[] commands = line.split(" && ");
        CommandExecutionResponse response = CommandExecutionResponse.SUCCESS;

        for (String command : commands) {
            CommandExecutionResponse singleResponse = this.dispatchSingleCommand(command, sender);
            if (singleResponse == CommandExecutionResponse.MISSING_PERMISSION) {
                response = CommandExecutionResponse.MISSING_PERMISSION;
            } else if (singleResponse == CommandExecutionResponse.COMMAND_NOT_FOUND && response != CommandExecutionResponse.MISSING_PERMISSION) {
                response = CommandExecutionResponse.COMMAND_NOT_FOUND;
            }
        }

        return response;
    }

    private CommandExecutionResponse dispatchSingleCommand(@NotNull String line, @NotNull CommandSender sender) {
        String[] args = CommandProperties.parseArgs(line);
        if (args.length == 0) {
            return CommandExecutionResponse.COMMAND_NOT_FOUND;
        }

        UsableCommand command = this.commands.get(args[0].toLowerCase());
        if (command == null) {
            return CommandExecutionResponse.COMMAND_NOT_FOUND;
        }

        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
            return CommandExecutionResponse.MISSING_PERMISSION;
        }

        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        try {
            command.execute(sender, commandName, args, line, CommandProperties.parseLine(args));
        } catch (Throwable throwable) {
            throw new CommandExecutionException(command, sender, line, throwable);
        }

        return CommandExecutionResponse.SUCCESS;
    }

    @Override
    public @NotNull Collection<UsableCommand> getCommands() {
        Collection<UsableCommand> commands = new ArrayList<>();
        for (UsableCommand command : this.commands.values()) {
            if (!commands.contains(command)) {
                commands.add(command);
            }
        }
        return commands;
    }

    @Override
    public @NotNull Collection<String> getCommandNames() {
        return Collections.unmodifiableCollection(this.commands.keySet());
    }

    @Override
    public @Nullable UsableCommand getCommand(@NotNull String name) {
        return this.commands.get(name.toLowerCase());
    }

    @Override
    public @Nullable UsableCommand parseCommand(@NotNull String line) {
        String[] args = CommandProperties.parseArgs(line);
        return args.length == 0 ? null : this.commands.get(args[0].toLowerCase());
    }

    @Override
    public @NotNull Collection<String> tabCompleteCommand(@NotNull String line) {
        String[] args = CommandProperties.parseArgs(line);
        if (args.length == 0) {
            return this.getCommandNames().stream().filter(name -> name != null && name.toLowerCase().startsWith(line.toLowerCase())).collect(Collectors.toList());
        } else {
            UsableCommand command = this.commands.get(args[0].toLowerCase());

            if (command instanceof TabCompleter) {
                String testString = args.length <= 1 || line.endsWith(" ") ? "" : args[args.length - 1].toLowerCase().trim();
                if (line.endsWith(" ")) {
                    args = Arrays.copyOfRange(args, 1, args.length + 1);
                    args[args.length - 1] = "";
                } else {
                    args = Arrays.copyOfRange(args, 1, args.length);
                }

                Collection<String> responses = ((TabCompleter) command).tabComplete(line, args, CommandProperties.parseLine(args));
                if (responses != null && !responses.isEmpty()) {
                    return responses.stream()
                            .filter(response -> response != null && (testString.isEmpty() || response.toLowerCase().startsWith(testString)))
                            .map(response -> response.contains(" ") ? "\"" + response + "\"" : response)
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }
}
