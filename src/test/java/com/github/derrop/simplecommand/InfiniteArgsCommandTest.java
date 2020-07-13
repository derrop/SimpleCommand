package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.map.CommandExecutionResponse;
import com.github.derrop.simplecommand.map.CommandMap;
import com.github.derrop.simplecommand.map.DefaultCommandMap;
import org.junit.Assert;
import org.junit.Test;

import static com.github.derrop.simplecommand.argument.DefaultArgumentTypes.anyStringIgnoreCase;
import static com.github.derrop.simplecommand.argument.DefaultArgumentTypes.dynamicString;

public class InfiniteArgsCommandTest {

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

        @SubCommand(args = {"arg1", "arg2", "arg3"}, minArgs = 3)
        public void handle(CommandArgumentWrapper args, String line) {
            Assert.assertEquals("some a1 arg2 any arg 1 2 3 4 5 6 7 8 9 10", line);

            Assert.assertEquals("any arg 1 2 3 4 5 6 7 8 9 10", args.argument("dyn_arg"));
        }

    }

    @Test
    public void testCommand() {
        CommandMap commandMap = new DefaultCommandMap();

        UsableCommand command = commandMap.registerSubCommands(new SomeCommand());

        UsableCommand newCommand = commandMap.getCommand("CoMmAnD");
        Assert.assertSame(newCommand, command);
        Assert.assertNotNull(command);

        Assert.assertEquals("some.permission", command.getPermission());
        Assert.assertEquals("some description", command.getDescription());
        Assert.assertEquals("some-prefix", command.getPrefix());
        Assert.assertEquals("some arg1 arg2 <dyn_arg> ... | some.permission | some description", command.getUsage());

        CommandExecutionResponse response = commandMap.dispatchConsoleCommand("some a1 arg2 any arg 1 2 3 4 5 6 7 8 9 10");
        Assert.assertEquals(CommandExecutionResponse.SUCCESS, response);
    }

}
