package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.map.CommandExecutionResponse;
import com.github.derrop.simplecommand.map.CommandMap;
import com.github.derrop.simplecommand.map.DefaultCommandMap;
import com.github.derrop.simplecommand.sender.CommandSender;
import com.github.derrop.simplecommand.sender.DefaultCollectionCommandSender;
import com.github.derrop.simplecommand.sender.DefaultConsoleCommandSender;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HelpCommandTest {

    @Test
    public void testCommand() {
        CommandMap commandMap = new DefaultCommandMap();

        commandMap.registerDefaultHelpCommand();

        UsableCommand command = commandMap.getCommand("help");
        Assert.assertNotNull(command);
        Assert.assertSame(command, commandMap.getCommand("?"));

        Assert.assertNull(command.getPermission());
        Assert.assertNull(command.getPrefix());
        Assert.assertEquals("Show a list of all commands or get help for a specific command", command.getDescription());

        List<String> messages = new ArrayList<>();
        CommandSender sender = new DefaultCollectionCommandSender(messages);

        CommandExecutionResponse response = commandMap.dispatchCommand("help", sender);
        Assert.assertEquals(CommandExecutionResponse.SUCCESS, response);

        Assert.assertEquals(2, messages.size());

        Assert.assertEquals("Aliases: [help, ?] - Show a list of all commands or get help for a specific command", messages.get(0));
        Assert.assertEquals(CommandTranslator.translateMessage("help-command-info"), messages.get(1));

        messages.clear();
        response = commandMap.dispatchCommand("help help", sender);
        Assert.assertEquals(CommandExecutionResponse.SUCCESS, response);

        Assert.assertEquals(6, messages.size());
        Assert.assertEquals(" ", messages.get(0));
        Assert.assertEquals("Aliases: [help, ?]", messages.get(1));
        Assert.assertEquals("Description: Show a list of all commands or get help for a specific command", messages.get(2));
        Assert.assertEquals("Usage: ", messages.get(3));
        Assert.assertEquals(" - help <command> | Get the usage for a specific command", messages.get(4));
        Assert.assertEquals(" - help  | Show a list of all commands", messages.get(5));
    }

}
