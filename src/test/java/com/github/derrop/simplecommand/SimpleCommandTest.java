package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.Handler;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.map.CommandExecutionResponse;
import com.github.derrop.simplecommand.map.CommandMap;
import com.github.derrop.simplecommand.map.DefaultCommandMap;
import com.github.derrop.simplecommand.sender.DefaultConsoleCommandSender;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static com.github.derrop.simplecommand.argument.DefaultArgumentTypes.*;

public class SimpleCommandTest {

    @Command(
            aliases = {"some", "command"},
            consoleOnly = true,
            permission = "some.permission",
            description = "some description",
            prefix = "some-prefix"
    )
    public static final class SomeCommand {

        @Argument
        public final ArgumentType<?> arg1 = anyStringIgnoreCase("arg1", "a1");
        @Argument
        public final ArgumentType<?> arg2 = anyStringIgnoreCase("arg2", "a2");
        @Argument
        public final ArgumentType<?> arg3 = dynamicString("dyn_arg");

        @Handler
        public void pre(Map<String, Object> internalProperties, String line) {
            Assert.assertEquals("some a1 arg2 \"any arg\"", line);
            internalProperties.put("a", "b");
        }

        @Handler
        public void post(Map<String, Object> internalProperties) {
            Assert.assertNull(internalProperties.get("a"));
        }

        @SubCommand(args = {"arg1", "arg2", "arg3"}, preHandlers = "pre", postHandlers = "post")
        public void handle(Map<String, Object> internalProperties) {
            Assert.assertEquals("b", internalProperties.get("a"));
            internalProperties.remove("a");
        }

    }

    @Test
    public void testCommand() {
        CommandMap commandMap = new DefaultCommandMap();

        UsableCommand command = Objects.requireNonNull(SubCommandPool.createSubCommandHandler(new SomeCommand()));
        commandMap.registerCommand(command);

        UsableCommand newCommand = commandMap.getCommand("CoMmAnD");
        Assert.assertSame(newCommand, command);

        Assert.assertEquals("some.permission", command.getPermission());
        Assert.assertEquals("some description", command.getDescription());
        Assert.assertEquals("some-prefix", command.getPrefix());
        Assert.assertEquals("some arg1 arg2 <dyn_arg> | some.permission | some description", command.getUsage());

        CommandExecutionResponse response = commandMap.dispatchConsoleCommand("some a1 arg2 \"any arg\"");
        Assert.assertEquals(CommandExecutionResponse.SUCCESS, response);

        response = commandMap.dispatchConsoleCommand("asdifuzasdfiuzsdas");
        Assert.assertEquals(CommandExecutionResponse.COMMAND_NOT_FOUND, response);

    }

}
