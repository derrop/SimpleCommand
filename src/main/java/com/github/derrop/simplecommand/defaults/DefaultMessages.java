package com.github.derrop.simplecommand.defaults;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultMessages {

    private static final Map<String, String> MESSAGES = new HashMap<>();
    public static final Function<String, String> FUNCTION = MESSAGES::get;

    static {
        registerMessage("help-command-not-found", "That command doesn't exist");
        registerMessage("help-command-info", "Use \"help <command>\" to get further information to a command");

        registerMessage("only-console", "This sub command is only available for the console!");
        registerMessage("no-permission", "You are not allowed to use this sub command!");

        registerMessage("argument-invalid-default", "Please provide a valid value!");
        registerMessage("argument-show-answers", "You have to provide one of the following: %values%");
        registerMessage("argument-invalid-url", "Please provide a valid URL!");
        registerMessage("argument-int-not-positive", "Please provide a positive integer!");
        registerMessage("argument-int-not-negative", "Please provide a negative integer!");
        registerMessage("argument-string-too-long", "The string cannot be longer than %maxLength% characters!");
        registerMessage("argument-invalid-uuid", "Please provide a valid UUID!");
        registerMessage("argument-invalid-int", "Please provide a valid integer!");
        registerMessage("argument-invalid-int-range", "Please provide a valid integer between %min% and %max%!");
        registerMessage("argument-invalid-double", "Please provide a valid decimal number!");
        registerMessage("argument-invalid-url", "Please provide a valid URL!");
        registerMessage("argument-invalid-boolean", "Please type in \"%true%\" or \"%false%\"!");
        registerMessage("argument-boolean-true", "yes");
        registerMessage("argument-boolean-false", "no");
    }

    private static void registerMessage(String key, String value) {
        MESSAGES.put(key, value);
    }

}
