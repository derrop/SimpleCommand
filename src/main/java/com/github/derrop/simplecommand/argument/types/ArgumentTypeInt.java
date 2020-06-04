package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.CommandTranslator;

import java.util.Collection;

public class ArgumentTypeInt implements ArgumentType<Integer> {

    @Override
    public boolean isValidInput(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public Integer parse(String input) {
        return Integer.parseInt(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return CommandTranslator.translateMessage("argument-invalid-int");
    }

}
