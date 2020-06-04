package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.defaults.DefaultMessages;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class CommandTranslator {

    public static final ExecutorService SERVICE = Executors.newCachedThreadPool();

    private static Function<String, String> MESSAGE_PROVIDER = DefaultMessages.FUNCTION;

    public static void useCustomMessages(@NotNull Function<String, String> messageProvider) {
        MESSAGE_PROVIDER = messageProvider;
    }

    public static String translateMessage(@NotNull String message) {
        return MESSAGE_PROVIDER.apply(message);
    }

}
