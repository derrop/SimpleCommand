package com.github.derrop.simplecommand.annotation.processor;

import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.Handler;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.executor.ReflectiveSubCommandExecutor;
import com.github.derrop.simplecommand.executor.SubCommandExecutor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessor {

    private final Object instance;

    private Command command;

    private final Map<String, ArgumentType<?>> arguments = new HashMap<>();
    private final Map<String, Method> handlers = new HashMap<>();

    private final Collection<ReadSubCommand> commands = new ArrayList<>();
    private final Collection<ProcessedSubCommand> processedSubCommands = new ArrayList<>();

    public AnnotationProcessor(Object instance) {
        this.instance = instance;
    }

    @Nullable
    public static ProcessedCommand process(Object instance) throws ReflectiveOperationException {
        AnnotationProcessor processor = new AnnotationProcessor(instance)
                .readCommand();
        if (processor.command == null) {
            return null;
        }

        return processor
                .readArguments()
                .readHandlers()
                .readSubCommands()
                .validate()
                .createSubCommands()
                .finish();
    }

    private AnnotationProcessor readCommand() {
        this.command = this.instance.getClass().getAnnotation(Command.class);
        return this;
    }

    private AnnotationProcessor readArguments() throws IllegalAccessException {
        for (Field field : this.instance.getClass().getFields()) {
            if (field.isAnnotationPresent(Argument.class)) {
                this.arguments.put(field.getName(), (ArgumentType<?>) field.get(this.instance));
            }
        }
        return this;
    }

    private AnnotationProcessor readHandlers() {
        for (Method method : this.instance.getClass().getMethods()) {
            if (method.isAnnotationPresent(Handler.class)) {
                this.handlers.put(method.getName(), method);
            }
        }
        return this;
    }

    private AnnotationProcessor readSubCommands() {
        for (Method method : this.instance.getClass().getMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                this.commands.add(new ReadSubCommand(method.getAnnotation(SubCommand.class), method));
            }
        }
        return this;
    }

    private AnnotationProcessor validate() {
        for (ReadSubCommand command : this.commands) {
            SubCommand subCommand = command.getSubCommand();
            for (String arg : subCommand.args()) {
                if (!this.arguments.containsKey(arg)) {
                    throw new IllegalArgumentException(String.format("SubCommand '%s' in Command '%s' is missing the argument '%s'", command.getMethod().getName(), this.instance.getClass().getSimpleName(), arg));
                }
            }

            for (String handler : subCommand.preHandlers()) {
                if (!this.handlers.containsKey(handler)) {
                    throw new IllegalArgumentException(String.format("SubCommand '%s' in Command '%s' is missing the preHandler '%s'", command.getMethod().getName(), this.instance.getClass().getSimpleName(), handler));
                }
            }

            for (String handler : subCommand.postHandlers()) {
                if (!this.handlers.containsKey(handler)) {
                    throw new IllegalArgumentException(String.format("SubCommand '%s' in Command '%s' is missing the postHandler '%s'", command.getMethod().getName(), this.instance.getClass().getSimpleName(), handler));
                }
            }
        }
        return this;
    }

    private AnnotationProcessor createSubCommands() {
        for (ReadSubCommand command : this.commands) {
            SubCommand subCommand = command.getSubCommand();

            ArgumentType<?>[] argumentTypes = this.getArgumentTypes(subCommand);
            Collection<Method> handlers = this.getHandlers(command);

            SubCommandExecutor executor = new ReflectiveSubCommandExecutor(this.instance, handlers);
            this.processedSubCommands.add(new ProcessedSubCommand(this.command, subCommand, argumentTypes, executor));
        }

        return this;
    }

    private ArgumentType<?>[] getArgumentTypes(SubCommand subCommand) {
        Collection<ArgumentType<?>> argumentTypes = new ArrayList<>(subCommand.args().length);
        for (String arg : subCommand.args()) {
            argumentTypes.add(this.arguments.get(arg));
        }
        return argumentTypes.toArray(new ArgumentType[0]);
    }

    private Collection<Method> getHandlers(ReadSubCommand command) {
        SubCommand subCommand = command.getSubCommand();

        Collection<Method> handlers = new ArrayList<>(subCommand.preHandlers().length + 1 + subCommand.postHandlers().length);
        for (String handler : subCommand.preHandlers()) {
            handlers.add(this.handlers.get(handler));
        }
        handlers.add(command.getMethod());
        for (String handler : subCommand.postHandlers()) {
            handlers.add(this.handlers.get(handler));
        }

        return handlers;
    }

    private ProcessedCommand finish() {
        return new ProcessedCommand(this.command, this.processedSubCommands);
    }

}
