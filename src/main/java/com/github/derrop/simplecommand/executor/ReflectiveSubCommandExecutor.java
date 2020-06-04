package com.github.derrop.simplecommand.executor;

import com.github.derrop.simplecommand.CommandInterrupt;
import com.github.derrop.simplecommand.CommandProperties;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ReflectiveSubCommandExecutor implements SubCommandExecutor {

    private static final Parameter[] ALLOWED_PARAMETERS;

    static {
        ALLOWED_PARAMETERS = SubCommandExecutor.class.getMethods()[0].getParameters();
    }

    private final Object instance;
    private final Collection<Method> methods;

    public ReflectiveSubCommandExecutor(Object instance, Collection<Method> methods) {
        for (Method method : methods) {
            this.validateParameters(method);
        }
        this.instance = instance;
        this.methods = methods;
    }

    private void validateParameters(Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (Arrays.stream(ALLOWED_PARAMETERS).noneMatch(allowed -> allowed.getType().equals(parameter.getType()))) {
                throw new IllegalArgumentException("Parameter " + parameter.getName() + "@" + parameter.getType().getSimpleName() + " is not allowed on sub commands");
            }
        }
    }

    @Override
    public void execute(CommandSender sender, String command, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties) {
        for (Method method : this.methods) {
            Parameter[] parameters = method.getParameters();
            Object[] values = new Object[method.getParameters().length];
            for (int i = 0; i < values.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                Object value;
                System.out.println(parameter.getType()+" - "+parameter.getName());
                if (CommandSender.class.isAssignableFrom(type)) {
                    value = sender;
                } else if (type.equals(String.class)) {
                    value = commandLine;
                } else if (type.equals(CommandArgumentWrapper.class)) {
                    value = args;
                } else if (type.equals(CommandProperties.class)) {
                    value = properties;
                } else if (type.equals(Map.class)) {
                    value = internalProperties;
                } else {
                    throw new IllegalStateException();
                }

                values[i] = value;
            }

            try {
                method.invoke(this.instance, values);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            } catch (CommandInterrupt interrupt) {
                break;
            }
        }
    }
}
