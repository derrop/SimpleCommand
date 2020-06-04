package com.github.derrop.simplecommand.argument.types;

import com.github.derrop.simplecommand.argument.ArgumentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArgumentTypeCollection implements ArgumentType<Collection<String>> {

    private Collection<String> possibleAnswers;
    private boolean allowEmpty = true;

    public ArgumentTypeCollection(Collection<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public ArgumentTypeCollection() {
    }

    public ArgumentTypeCollection disallowEmpty() {
        this.allowEmpty = false;
        return this;
    }

    @Override
    public boolean isValidInput(String input) {
        if (!this.allowEmpty && input.trim().isEmpty()) {
            return false;
        }
        return this.possibleAnswers == null || Arrays.stream(input.split(";")).allMatch(entry -> this.possibleAnswers.contains(entry));
    }

    @Override
    public Collection<String> parse(String input) {
        return new ArrayList<>(Arrays.asList(input.split(";")));
    }

    @Override
    public List<String> getCompletableAnswers() {
        return this.possibleAnswers != null ? new ArrayList<>(this.possibleAnswers) : null;
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return this.possibleAnswers;
    }

}
