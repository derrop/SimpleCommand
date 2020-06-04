package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ArgumentTypeStaticStringArray implements ArgumentType<String> {

    private final String[] allowedValues;
    private final boolean ignoreCase;

    public ArgumentTypeStaticStringArray(String[] allowedValues, boolean ignoreCase) {
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one value has to be provided");
        }
        this.allowedValues = allowedValues;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean isValidInput(String input) {
        return Arrays.stream(this.allowedValues)
                .anyMatch(value -> (this.ignoreCase && value.equalsIgnoreCase(input)) || value.equals(input));
    }

    @Override
    public String parse(String input) {
        return Arrays.stream(this.allowedValues).filter(value -> value.equalsIgnoreCase(input)).findFirst().get();
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return null;
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return Collections.singletonList(this.allowedValues[0]);
    }
}
