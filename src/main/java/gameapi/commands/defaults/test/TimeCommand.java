package gameapi.commands.defaults.test;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.CalendarTools;

/**
 * @author glorydark
 */
public class TimeCommand extends EasySubCommand {

    public TimeCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        commandSender.sendMessage(CalendarTools.getCachedBeijingTime().getTime().toString());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
