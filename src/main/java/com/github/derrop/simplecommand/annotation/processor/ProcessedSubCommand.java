package com.github.derrop.simplecommand.annotation.processor;

import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.UsableCommand;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.argument.CommandArgument;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.argument.DefaultArgumentTypes;
import com.github.derrop.simplecommand.argument.types.ArgumentTypeStaticStringArray;
import com.github.derrop.simplecommand.executor.SubCommandExecutor;

import java.util.*;

/**
 * Represents a sub command of any command.
 */
public class ProcessedSubCommand implements SubCommandExecutor {

    private final String permission;
    private final String description;
    private final String extendedUsage;

    private final boolean consoleOnly;
    private final boolean propertiesEnabled;
    private final boolean async;
    private final boolean disableMinArgsIndicator;

    private int minArgs = -1;
    private final int maxArgs;
    private final ArgumentType<?>[] requiredArguments;

    private final SubCommandExecutor wrappedExecutor;

    public ProcessedSubCommand(Command command, SubCommand subCommand, ArgumentType<?>[] requiredArguments, SubCommandExecutor wrappedExecutor) {
        this.requiredArguments = requiredArguments;
        this.permission = this.or(subCommand.permission(), command.permission());
        this.description = this.or(subCommand.description(), command.description());
        this.extendedUsage = subCommand.extendedUsage();
        this.consoleOnly = subCommand.consoleOnly() || command.consoleOnly();
        this.propertiesEnabled = subCommand.enableProperties();
        this.async = subCommand.async();
        this.disableMinArgsIndicator = !subCommand.showMinArgsIndicator();

        this.minArgs = subCommand.minArgs();
        this.maxArgs = subCommand.maxArgs();

        this.wrappedExecutor = wrappedExecutor;
    }

    private String or(String string1, String string2) {
        return string2.isEmpty() && string1.isEmpty() ? null : string1.isEmpty() ? string2 : string1;
    }

    /**
     * Checks whether this command requires the exact length of input arguments or not.
     *
     * @return {@code true} if this command requires it or {@code false} if not
     */
    public boolean requiresExactArgs() {
        return this.minArgs == -1 && this.maxArgs == -1;
    }

    /**
     * Checks whether this command requires a minimal length of input argument.
     *
     * @return {@code true} if this command requires it or {@code false} if not
     */
    public boolean requiresMinArgs() {
        return this.minArgs != -1;
    }

    public boolean mayLastArgContainSpaces() {
        return this.requiresMinArgs() && this.minArgs == this.requiredArguments.length;
    }

    /**
     * Checks whether this command requires a maximum length of input arguments.
     *
     * @return {@code true} if this command requires it or {@code false} if not
     */
    public boolean requiresMaxArgs() {
        return this.maxArgs != -1;
    }

    /**
     * Checks whether the input length of arguments is valid according to the exact, min and max length of arguments specified for this command.
     *
     * @param length the input arguments length
     * @return {@code true} if the length matches or {@code false} if not
     * @see #requiresExactArgs()
     * @see #requiresMinArgs()
     * @see #requiresMaxArgs()
     */
    public boolean checkValidArgsLength(int length) {
        if (this.requiresMinArgs() && length < this.minArgs) {
            return false;
        }
        if (this.propertiesEnabled && length >= (this.requiresExactArgs() ? this.requiredArguments.length : this.minArgs)) {
            return true;
        }
        return (!this.requiresExactArgs() || this.requiredArguments.length == length) && (!this.requiresMaxArgs() || length <= this.maxArgs);
    }

    //the returned pair contains the message of the first non-matching argument and the amount of non-matching, static arguments
    public InvalidArgumentMessage getInvalidArgumentMessage(String[] args) {
        String resultMessage = null;
        int nonMatched = 0;
        for (int i = 0; i < args.length; i++) {
            if (this.propertiesEnabled && i >= this.getBeginOfProperties()) {
                break;
            }

            if (this.requiredArguments.length > i) {

                ArgumentType<?> type = this.requiredArguments[i];

                if (!type.isValidInput(args[i])) {
                    if (type instanceof ArgumentTypeStaticStringArray) {
                        ++nonMatched;
                    }

                    String invalidMessage = type.getInvalidInputMessage(args[i]);
                    if (invalidMessage != null && resultMessage == null) {
                        resultMessage = invalidMessage;
                    }
                }

            } else {

                if (this.requiredArguments.length == 0) {
                    continue;
                }

                String currentValue = String.join(" ", Arrays.copyOfRange(args, Math.max(0, i - 1), Math.max(this.requiredArguments.length, Math.min(args.length, this.maxArgs))));
                ArgumentType<?> type = this.requiredArguments[this.requiredArguments.length - 1];

                if (!type.isValidInput(currentValue)) {
                    if (type instanceof ArgumentTypeStaticStringArray) {
                        ++nonMatched;
                    }

                    String invalidMessage = type.getInvalidInputMessage(currentValue);
                    if (invalidMessage != null && resultMessage == null) {
                        resultMessage = invalidMessage;
                    }
                }

                break;

            }
        }
        return resultMessage != null ? new InvalidArgumentMessage(resultMessage, nonMatched) : null;
    }

    public CommandArgument<?>[] parseArgs(String[] args) {
        if (!this.checkValidArgsLength(args.length)) {
            return null;
        }
        return this.parseArgsIgnoreLength(args);
    }

    public CommandArgument<?>[] parseArgsIgnoreLength(String[] args) {
        List<CommandArgument<?>> result = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (this.propertiesEnabled && i >= this.getBeginOfProperties()) {
                break;
            }
            if (this.requiredArguments.length > i) {
                ArgumentType<?> type = this.requiredArguments[i];
                if (!type.isValidInput(args[i])) {
                    return null;
                }

                result.add(new CommandArgument(type, type.parse(args[i])));
            } else {
                String currentValue = String.join(" ", Arrays.copyOfRange(args, Math.max(0, i - 1), Math.max(this.requiredArguments.length, Math.min(args.length, this.maxArgs == -1 ? Integer.MAX_VALUE : this.maxArgs))));
                ArgumentType<?> type = this.requiredArguments[this.requiredArguments.length - 1];

                if (type.isValidInput(currentValue)) {
                    result.set(result.size() - 1, new CommandArgument(type, type.parse(currentValue)));
                } else {
                    return null;
                }

                break;
            }
        }
        return result.toArray(new CommandArgument[0]);
    }

    public CommandProperties parseProperties(String[] args) {
        if (!this.propertiesEnabled) {
            return null;
        }
        if (this.getBeginOfProperties() > args.length) {
            return CommandProperties.create();
        }
        return CommandProperties.parseLine(Arrays.copyOfRange(args, this.getBeginOfProperties(), args.length));
    }

    private int getBeginOfProperties() {
        return (this.requiresMaxArgs() ? this.maxArgs : this.minArgs == -1 ? this.requiredArguments.length : this.minArgs);
    }

    /**
     * Gets the possible answers for the next argument by the given args array for auto completion.
     * All arguments in the array have to match to get any response.
     *
     * @param args the current args to get the next argument from
     * @return a list containing all possible answers or null, if no argument was found or the next argument doesn't have any answers defined
     */
    public Collection<String> getNextPossibleArgumentAnswers(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (this.requiredArguments.length > i) {
                if (!this.requiredArguments[i].isValidInput(args[i])) {
                    return null;
                }
            }
        }
        if (this.requiredArguments.length >= args.length) {
            return this.requiredArguments[args.length - 1].getPossibleAnswers();
        }
        return null;
    }

    /**
     * Gets all arguments of this sub command as a string.<br>
     * Static strings ({@link DefaultArgumentTypes#anyString(String...)} or {@link DefaultArgumentTypes#anyStringIgnoreCase(String...)})
     * are never wrapped with brackets.<br>
     * <br>
     * Dynamic strings (any other {@link ArgumentType}) are wrapped with "&lt;&gt;" if they are required and
     * with "[]" if they are optional (this can be set with the min args and max args in the {@link SubCommand} annotation).
     *
     * @return the arguments as a string split by spaces (the argument
     */
    public String getArgsAsString() {
        Collection<String> args = new ArrayList<>();
        int i = 0;
        for (ArgumentType<?> requiredArgument : this.requiredArguments) {

            String usage = requiredArgument.getUsageDisplay();
            Collection<String> possibleAnswers = requiredArgument.getPossibleAnswers();

            boolean required = this.requiresExactArgs() || (this.requiresMinArgs() && i + 1 <= this.minArgs);
            String answer;
            if (possibleAnswers == null || possibleAnswers.isEmpty() || usage != null) {

                answer = usage;
                answer = required ? ("<" + answer + ">") : ("[" + answer + "]");

            } else {
                if (possibleAnswers.size() == 1) {
                    answer = possibleAnswers.iterator().next();
                } else {
                    answer = String.join(", ", possibleAnswers);
                    answer = required ? ("<" + answer + ">") : ("[" + answer + "]");
                }
            }

            if (!this.disableMinArgsIndicator && this.requiresMinArgs() && i == this.minArgs - 1 && this.minArgs == this.requiredArguments.length) {
                answer += " ...";
            }

            args.add(answer);
            ++i;
        }
        return String.join(" ", args);
    }

    public boolean isConsoleOnly() {
        return this.consoleOnly;
    }

    public boolean isAsync() {
        return this.async;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExtendedUsage() {
        return this.extendedUsage;
    }

    public int getMinArgs() {
        return this.minArgs;
    }

    public int getMaxArgs() {
        return this.maxArgs;
    }

    public ArgumentType<?>[] getRequiredArguments() {
        return this.requiredArguments;
    }

    @Override
    public void execute(UsableCommand usableCommand, CommandSender sender, String command, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties) {
        this.wrappedExecutor.execute(usableCommand, sender, command, args, commandLine, properties, internalProperties);
    }
}
