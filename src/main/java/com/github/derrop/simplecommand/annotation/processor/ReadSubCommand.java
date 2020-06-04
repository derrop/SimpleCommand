package com.github.derrop.simplecommand.annotation.processor;

import com.github.derrop.simplecommand.annotation.SubCommand;

import java.lang.reflect.Method;

public class ReadSubCommand {

    private SubCommand subCommand;
    private Method method;

    public ReadSubCommand(SubCommand subCommand, Method method) {
        this.subCommand = subCommand;
        this.method = method;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public Method getMethod() {
        return method;
    }
}
