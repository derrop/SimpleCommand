package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.SubCommandPool;

import java.util.Collection;
import java.util.UUID;

public class ArgumentTypeUUID implements ArgumentType<UUID> {

    @Override
    public boolean isValidInput(String input) {
        return this.parse(input) != null;
    }

    @Override
    public UUID parse(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return SubCommandPool.translateMessage("argument-invalid-uuid");
    }
}
