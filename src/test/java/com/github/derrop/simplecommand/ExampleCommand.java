package com.github.derrop.simplecommand;

import com.github.derrop.simplecommand.annotation.Argument;
import com.github.derrop.simplecommand.annotation.Command;
import com.github.derrop.simplecommand.annotation.Handler;
import com.github.derrop.simplecommand.annotation.SubCommand;
import com.github.derrop.simplecommand.argument.ArgumentRequirement;
import com.github.derrop.simplecommand.argument.ArgumentType;
import com.github.derrop.simplecommand.argument.CommandArgumentWrapper;
import com.github.derrop.simplecommand.sender.CommandSender;

import java.util.Map;

import static com.github.derrop.simplecommand.argument.DefaultArgumentTypes.*;

@Command(
        aliases = "example",
        permission = "command.example",
        description = "Description for this command",
        prefix = "command-prefix",
        consoleOnly = false
)
public class ExampleCommand {

    @Argument
    public final ArgumentType<?> set = anyStringIgnoreCase("set", "modify");
    @Argument
    public final ArgumentType<?> value = anyStringIgnoreCase("value", "val");
    @Argument
    public final ArgumentType<?> newValue = positiveInteger("newValue");

    @Handler
    public void preHandle(CommandSender sender, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties) {
        // you don't have to provide any of those parameters, you can choose whatever you want. You can also use 0 parameters

        sender.sendMessage("This method is being called first!");
    }

    @Handler
    public void postHandle(CommandSender sender, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties) {
        // you don't have to provide any of those parameters, you can choose whatever you want. You can also use 0 parameters

        sender.sendMessage("This method is being called last!");
    }

    @SubCommand(
            args = {"set", "value", "newValue"}, // the keys of the arguments from above
            preHandlers = "preHandle", // the names of the methods from above
            postHandlers = "postHandle", // the names of the methods from above
            consoleOnly = false,
            description = "Set the new value",
            permission = "command.example.set.value",
            requirement = ArgumentRequirement.EXACT, // does the command need an exact amount of arguments or can the user provide infinity arguments?
            maxArgs = -1, // ignored if the "requirement" is set to EXACT
            showMinArgsIndicator = true, // this will show a "..." at the end of the usage if the "requirement is set to "MINIMUM"
            async = false, // whether the command should be called async from the dispatchCommand method
            enableProperties = false, // if false, the "properties" in the parameters will be null
            extendedUsage = " | this will be appended to the usage of this command"
    )
    public void setValueCommand(CommandSender sender, CommandArgumentWrapper args, String commandLine, CommandProperties properties, Map<String, Object> internalProperties) {
        // you don't have to provide any of those parameters, you can choose whatever you want. You can also use 0 parameters


        int newValue = (int) args.argument("newValue");
    }

}
