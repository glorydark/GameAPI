package gameapi.commands.sub;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {

        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
