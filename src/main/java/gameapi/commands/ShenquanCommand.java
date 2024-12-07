package gameapi.commands;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/**
 * @author Glorydark
 * For in-game test
 */
public class ShenquanCommand extends Command {

    public ShenquanCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Server.getInstance().dispatchCommand(commandSender, "gameapi shenquan");
        return false;
    }
}
