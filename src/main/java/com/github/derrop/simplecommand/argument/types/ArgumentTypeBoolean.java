package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.SubCommandPool;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArgumentTypeBoolean implements ArgumentType<Boolean> {
    private final String trueString;
    private final String falseString;

    public ArgumentTypeBoolean(String trueString, String falseString) {
        this.trueString = trueString != null ? trueString : "yes";
        this.falseString = falseString != null ? falseString : "no";
    }

    public ArgumentTypeBoolean() {
        this(SubCommandPool.translateMessage("argument-boolean-true"), SubCommandPool.translateMessage("argument-boolean-false"));
    }

    public String getTrueString() {
        return this.trueString;
    }

    public String getFalseString() {
        return this.falseString;
    }

    @Override
    public boolean isValidInput(String input) {
        return this.trueString.equalsIgnoreCase(input) || this.falseString.equalsIgnoreCase(input);
    }

    @Override
    public Boolean parse(String input) {
        return this.trueString.equalsIgnoreCase(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return this.getCompletableAnswers();
    }

    @Override
    public List<String> getCompletableAnswers() {
        return Arrays.asList(this.trueString, this.falseString);
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return SubCommandPool.translateMessage("argument-invalid-boolean")
                .replace("%true%", this.trueString)
                .replace("%false%", this.falseString);
    }

}
