package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.activity.ActivityMain;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
@Deprecated
public class FunctionActivityCommand extends EasySubCommand {

    public FunctionActivityCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        ActivityMain.showActivityMain((Player) commandSender);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
