package com.github.derrop.simplecommand;

import java.util.*;

public class CommandProperties {

    private final Map<String, String> properties;

    private CommandProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public static CommandProperties create() {
        return new CommandProperties(new HashMap<>());
    }

    public static String[] parseArgs(String line) {
        if (line.trim().isEmpty()) {
            return new String[0];
        }

        Collection<String> args = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        char[] chars = line.toCharArray();
        boolean inQuote = false;

        for (char c : chars) {
            if (c == '"') {
                inQuote = !inQuote;
                continue;
            }

            if (!inQuote && c == ' ') {
                args.add(builder.toString());
                builder.setLength(0);
                continue;
            }

            builder.append(c);
        }

        if (builder.length() != 0) {
            args.add(builder.toString());
        }

        return args.toArray(new String[0]);
    }

    public static CommandProperties parseLine(String line) {
        return parseLine(parseArgs(line));
    }

    public static CommandProperties parseLine(String[] args) {
        Map<String, String> properties = new HashMap<>();

        for (String argument : args) {
            if (argument.isEmpty() || argument.equals(" ")) {
                continue;
            }

            if (argument.contains("=")) {
                int x = argument.indexOf("=");
                properties.put(argument.substring(0, x).replaceFirst("-", "").replaceFirst("-", ""), argument.substring(x + 1));
                continue;
            }

            if (argument.contains("--") || argument.contains("-")) {
                properties.put(argument.replaceFirst("-", "").replaceFirst("-", ""), "true");
                continue;
            }

            properties.put(argument, "true");
        }

        return new CommandProperties(properties);
    }

    public String getString(String key) {
        return this.properties.get(key);
    }

    public String getString(String key, String def) {
        return this.properties.getOrDefault(key, def);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.getString(key));
    }

    public int getInt(String key, int def) {
        try {
            return Integer.parseInt(this.getString(key));
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public int getInt(String key) {
        return this.getInt(key, -1);
    }

    public long getLong(String key, long def) {
        try {
            return Long.parseLong(this.getString(key));
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public long getLong(String key) {
        return this.getLong(key, -1L);
    }

    public double getDouble(String key, double def) {
        try {
            return Double.parseDouble(this.getString(key));
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public double getDouble(String key) {
        return this.getDouble(key, -1D);
    }

}
