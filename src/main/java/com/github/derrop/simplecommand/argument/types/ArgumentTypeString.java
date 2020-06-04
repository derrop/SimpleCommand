package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;

import java.util.Collection;

public class ArgumentTypeString implements ArgumentType<String> {

    @Override
    public boolean isValidInput(String input) {
        return true;
    }

    @Override
    public String parse(String input) {
        return input;
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

}
