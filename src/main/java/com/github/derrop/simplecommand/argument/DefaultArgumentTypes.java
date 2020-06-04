package com.github.derrop.simplecommand.argument;

import com.github.derrop.simplecommand.argument.types.*;
import com.github.derrop.simplecommand.SubCommandPool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultArgumentTypes {

    public static ArgumentType<Collection<String>> collection(String key) {
        return collection(null, key);
    }

    public static ArgumentType<Collection<String>> collection(Collection<String> possibleAnswers, String key) {
        return collection(possibleAnswers, key, null);
    }

    public static ArgumentType<Collection<String>> collection(Collection<String> possibleAnswers, String key, String invalidMessage) {
        return new ArgumentTypeCollection(possibleAnswers) {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage != null ? invalidMessage : super.getInvalidInputMessage(input);
            }
        };
    }

    public static <E extends Enum<E>> ArgumentType<E> exactEnum(Class<E> enumClass) {
        return new ArgumentTypeEnum<>(enumClass);
    }

    public static ArgumentType<String> anyStringIgnoreCase(String... allowedValues) {
        return new ArgumentTypeStaticStringArray(allowedValues, true);
    }

    public static ArgumentType<String> anyString(String... allowedValues) {
        return new ArgumentTypeStaticStringArray(allowedValues, false);
    }

    public static ArgumentType<String> url(String key) {
        return dynamicString(key, SubCommandPool.translateMessage("argument-invalid-url"), input -> {
            try {
                new URL(input);
                return true;
            } catch (MalformedURLException exception) {
                return false;
            }
        });
    }

    public static ArgumentType<String> dynamicString(String key) {
        return dynamicString(key, null, s -> true);
    }

    public static ArgumentType<String> dynamicString(String key, Predicate<String> tester) {
        return dynamicString(key, null, tester);
    }

    public static ArgumentType<String> dynamicString(String key, int maxLength) {
        return dynamicString(
                key,
                SubCommandPool.translateMessage("argument-string-too-long")
                        .replace("%maxLength%", String.valueOf(maxLength)),
                value -> value.length() <= maxLength
        );
    }

    public static ArgumentType<String> dynamicString(String key, String invalidMessage, Predicate<String> tester) {
        return dynamicString(key, invalidMessage, tester, null);
    }

    public static ArgumentType<String> dynamicString(String key, Supplier<Collection<String>> possibleAnswersSupplier) {
        return dynamicString(key, null, s -> true, possibleAnswersSupplier);
    }

    public static ArgumentType<String> dynamicString(String key, String invalidMessage, Predicate<String> tester, Supplier<Collection<String>> possibleAnswersSupplier) {
        return new ArgumentTypeString() {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public boolean isValidInput(String input) {
                return tester.test(input);
            }

            @Override
            public Collection<String> getPossibleAnswers() {
                return possibleAnswersSupplier != null ? possibleAnswersSupplier.get() : null;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage;
            }
        };
    }

    public static ArgumentType<Double> double_(String key) {
        return new ArgumentTypeDouble() {
            @Override
            public String getUsageDisplay() {
                return key;
            }
        };
    }

    public static ArgumentType<Double> double_(String key, String invalidMessage) {
        return double_(key, invalidMessage, null);
    }

    public static ArgumentType<Double> double_(String key, String invalidMessage, Predicate<Double> tester) {
        return double_(key, invalidMessage, tester, null);
    }

    public static ArgumentType<Double> double_(String key, String invalidMessage, Predicate<Double> tester, Supplier<Collection<Double>> possibleAnswersSupplier) {
        return new ArgumentTypeDouble() {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage;
            }

            @Override
            public Collection<String> getPossibleAnswers() {
                return possibleAnswersSupplier != null ? possibleAnswersSupplier.get().stream().map(Objects::toString).collect(Collectors.toList()) : null;
            }

            @Override
            public boolean isValidInput(String input) {
                return super.isValidInput(input) && tester.test(Double.parseDouble(input));
            }
        };
    }

    public static ArgumentType<Integer> positiveInteger(String key) {
        return integer(
                key,
                SubCommandPool.translateMessage("argument-int-not-positive"),
                value -> value > 0
        );
    }

    public static ArgumentType<Integer> negativeInteger(String key) {
        return integer(
                key,
                SubCommandPool.translateMessage("argument-int-not-negative"),
                value -> value < 0
        );
    }

    public static ArgumentType<Integer> integer(String key, int minValue, int maxValue) {
        return integer(key, null, minValue, maxValue);
    }

    public static ArgumentType<Integer> integer(String key, String invalidMessage, int minValue, int maxValue) {
        return new ArgumentTypeIntRange(minValue, maxValue) {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage != null ? invalidMessage : super.getInvalidInputMessage(input);
            }
        };
    }

    public static ArgumentType<Boolean> bool(String key) {
        return bool(key, null);
    }

    public static ArgumentType<Boolean> bool(String key, String invalidMessage) {
        return new ArgumentTypeBoolean("true", "false") {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage != null ? invalidMessage : super.getInvalidInputMessage(input);
            }
        };
    }

    public static ArgumentType<Integer> integer(String key) {
        return new ArgumentTypeInt() {
            @Override
            public String getUsageDisplay() {
                return key;
            }
        };
    }

    public static ArgumentType<Integer> integer(String key, String invalidMessage) {
        return integer(key, invalidMessage, input -> true);
    }

    public static ArgumentType<Integer> integer(String key, Predicate<Integer> tester) {
        return integer(key, null, tester);
    }

    public static ArgumentType<Integer> integer(String key, String invalidMessage, Predicate<Integer> tester) {
        return integer(key, invalidMessage, tester, null);
    }

    public static ArgumentType<Integer> integer(String key, Predicate<Integer> tester, Supplier<Collection<Integer>> possibleAnswersSupplier) {
        return integer(key, null, tester, possibleAnswersSupplier);
    }

    public static ArgumentType<Integer> integer(String key, String invalidMessage, Predicate<Integer> tester, Supplier<Collection<Integer>> possibleAnswersSupplier) {
        return new ArgumentTypeInt() {
            @Override
            public String getUsageDisplay() {
                return key;
            }

            @Override
            public String getInvalidInputMessage(String input) {
                return invalidMessage != null ? invalidMessage : super.getInvalidInputMessage(input);
            }

            @Override
            public Collection<String> getPossibleAnswers() {
                return possibleAnswersSupplier != null ? possibleAnswersSupplier.get().stream().map(Objects::toString).collect(Collectors.toList()) : null;
            }

            @Override
            public boolean isValidInput(String input) {
                return super.isValidInput(input) && tester.test(Integer.parseInt(input));
            }
        };
    }

}
