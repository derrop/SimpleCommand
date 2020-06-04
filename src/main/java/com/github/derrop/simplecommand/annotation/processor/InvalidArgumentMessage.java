package com.github.derrop.simplecommand.annotation.processor;

public class InvalidArgumentMessage {

    private final String message;
    private final int nonMatchedStaticValues;

    public InvalidArgumentMessage(String message, int nonMatchedStaticValues) {
        this.message = message;
        this.nonMatchedStaticValues = nonMatchedStaticValues;
    }

    public String getMessage() {
        return this.message;
    }

    public int getNonMatchedStaticValues() {
        return this.nonMatchedStaticValues;
    }

}
