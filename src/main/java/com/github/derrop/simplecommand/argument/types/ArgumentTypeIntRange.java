package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.CommandTranslator;

public class ArgumentTypeIntRange extends ArgumentTypeInt {
    private final int minValue;
    private final int maxValue;

    public ArgumentTypeIntRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= this.minValue && value <= this.maxValue;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public Integer parse(String input) {
        return Integer.parseInt(input);
    }

    @Override
    public String getPossibleAnswersAsString() {
        return "[" + this.minValue + ", " + this.maxValue + "]";
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return CommandTranslator.translateMessage("argument-invalid-int-range")
                .replace("%min%", String.valueOf(this.minValue))
                .replace("%max%", String.valueOf(this.maxValue));
    }

}
