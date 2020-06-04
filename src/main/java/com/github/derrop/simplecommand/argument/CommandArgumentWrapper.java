package com.github.derrop.simplecommand.argument;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class CommandArgumentWrapper {
    private final CommandArgument<?>[] arguments;

    public CommandArgumentWrapper(CommandArgument<?>[] arguments) {
        this.arguments = arguments;
    }

    public CommandArgumentWrapper(Collection<CommandArgument<?>> arguments) {
        this(arguments.toArray(new CommandArgument[0]));
    }

    public Object[] array() {
        Object[] objects = new Object[this.arguments.length];
        for (int i = 0; i < this.arguments.length; i++) {
            objects[i] = this.arguments[i].getAnswer();
        }
        return objects;
    }

    public int length() {
        return this.arguments.length;
    }

    public ArgumentType<?> argumentType(int index) {
        return this.hasArgument(index) ? this.arguments[index].getAnswerType() : null;
    }

    public Object argument(int index) {
        return this.hasArgument(index) ? this.arguments[index].getAnswer() : null;
    }

    public boolean hasArgument(int index) {
        return this.arguments.length > index && index >= 0;
    }


    private Stream<CommandArgument<?>> argumentStream(String key) {
        return Arrays.stream(this.arguments)
                .filter(argument -> argument.getAnswerType().getUsageDisplay() != null)
                .filter(argument -> argument.getAnswerType().getUsageDisplay().equalsIgnoreCase(key));
    }

    public Optional<ArgumentType<?>> argumentType(String key) {
        return this.argumentStream(key)
                .findFirst()
                .map(CommandArgument::getAnswerType);
    }

    public Optional<Object> optionalArgument(String key) {
        return this.argumentStream(key)
                .findFirst()
                .map(CommandArgument::getAnswer);
    }

    @NotNull
    public Object argument(String key) {
        return this.argumentStream(key)
                .findFirst()
                .map(CommandArgument::getAnswer)
                .orElseThrow(() -> new IllegalArgumentException("No argument with the key '" + key + "' has been provided"));
    }

    public Optional<Object> argument(Class<? extends ArgumentType<?>> answerTypeClass) {
        return Arrays.stream(this.arguments)
                .filter(argument -> answerTypeClass.isAssignableFrom(argument.getAnswerType().getClass()))
                .findFirst()
                .map(CommandArgument::getAnswer);
    }

    public boolean hasArgument(String key) {
        return this.optionalArgument(key).isPresent();
    }


}
