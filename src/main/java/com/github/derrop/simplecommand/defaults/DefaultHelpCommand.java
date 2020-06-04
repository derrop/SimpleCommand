package com.github.derrop.simplecommand.defaults;

import com.github.derrop.simplecommand.CommandTranslator;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.argument.DefaultArgumentTypes;
import com.github.derrop.simplecommand.map.CommandMap;
import com.github.derrop.simplecommand.sender.CommandSender;

import java.util.Arrays;

@Command(
        aliases = {"help", "?"},
        description = "Show a list of all commands or get help for a specific command"
)
public class DefaultHelpCommand {

    private final CommandMap commandMap;

    public DefaultHelpCommand(CommandMap commandMap) {
        this.commandMap = commandMap;

        this.command = DefaultArgumentTypes.dynamicString(
                "command",
                CommandTranslator.translateMessage("help-command-not-found"),
                s -> this.commandMap.getCommand(s) != null,
                this.commandMap::getCommandNames
        );
    }

    @Argument
    public final ArgumentType<?> command;


    @SubCommand(
            description = "Show a list of all commands"
    )
    public void handleMain(CommandSender sender) {
        UsableCommand[] commands = this.commandMap.getCommands().toArray(new UsableCommand[0]);
        StringBuilder[] entries = new StringBuilder[commands.length];
        
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new StringBuilder();
        }

        for (int i = 0; i < commands.length; i++) {
            entries[i].append("Aliases: ").append(Arrays.toString(commands[i].getAliases()));
        }

        int maxLength = Arrays.stream(entries).mapToInt(StringBuilder::length).max().orElse(0);

        this.fill(maxLength, entries);

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getPermission() != null) {
                entries[i].append(" | Permission: ").append(commands[i].getPermission());
            }
        }

        maxLength = Arrays.stream(entries).mapToInt(StringBuilder::length).max().orElse(0);

        this.fill(maxLength, entries);

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getDescription() != null) {
                entries[i].append(" - ").append(commands[i].getDescription());
            }
        }

        for (StringBuilder entry : entries) {
            sender.sendMessage(entry.toString());
        }
        sender.sendMessage(CommandTranslator.translateMessage("help-command-info"));
    }

    @SubCommand(
            args = "command",
            description = "Get the usage for a specific command"
    )
    public void handleCommand(CommandSender sender, CommandArgumentWrapper args) {
        UsableCommand command = this.commandMap.getCommand((String) args.argument("command"));
        if (command == null) {
            return;
        }

        String usage = command.getUsage();
        String description = command.getDescription();

        sender.sendMessage(" ", "Aliases: " + Arrays.toString(command.getAliases()));
        if (description != null) {
            sender.sendMessage("Description: " + description);
        }
        if (usage != null) {
            String[] usageLines = ("Usage: " + usage).split("\n");
            for (String line : usageLines) {
                sender.sendMessage(line);
            }
        }
    }

    private void fill(int maxLength, StringBuilder[] entries) {
        for (StringBuilder entry : entries) {
            int missing = maxLength - entry.length();
            for (int j = 0; j < missing; j++) {
                entry.append(' ');
            }
        }
    }

}
