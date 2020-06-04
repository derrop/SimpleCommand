package com.github.derrop.simplecommand.argument;

public class CommandArgument<T> {
    private final ArgumentType<T> answerType;
    private final T answer;

    public CommandArgument(ArgumentType<T> answerType, T answer) {
        this.answerType = answerType;
        this.answer = answer;
    }

    public ArgumentType<T> getAnswerType() {
        return this.answerType;
    }

    public T getAnswer() {
        return this.answer;
    }
}
