package com.github.derrop.simplecommand.argument;

import com.github.derrop.simplecommand.SubCommandPool;

import java.util.Collection;
import java.util.List;

public interface ArgumentType<T> {

    boolean isValidInput(String input);

    T parse(String input);

    /**
     * @return null if there are infinite possible answers
     */
    Collection<String> getPossibleAnswers();

    default List<String> getCompletableAnswers() {
        return null;
    }

    default String getUsageDisplay() {
        return null;
    }

    default String getPossibleAnswersAsString() {
        Collection<String> possibleAnswers = this.getPossibleAnswers();
        return possibleAnswers != null ? String.join(", ", possibleAnswers) : null;
    }

    default String getInvalidInputMessage(String input) {
        Collection<String> possibleAnswers = this.getPossibleAnswers();
        if (possibleAnswers != null) {
            return SubCommandPool.translateMessage("argument-show-answers").replace("%values%", this.getPossibleAnswersAsString());
        }
        return SubCommandPool.translateMessage("argument-invalid-default");
    }

}
