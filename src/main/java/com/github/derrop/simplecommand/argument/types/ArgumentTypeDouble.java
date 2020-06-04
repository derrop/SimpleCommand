package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.CommandTranslator;

import java.util.Collection;

public class ArgumentTypeDouble implements ArgumentType<Double> {

    @Override
    public boolean isValidInput(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public Double parse(String input) {
        return Double.parseDouble(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return CommandTranslator.translateMessage("argument-invalid-double");
    }

}
