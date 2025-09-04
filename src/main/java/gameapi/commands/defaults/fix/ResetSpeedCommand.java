package gameapi.commands.defaults.fix;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.PlayerTools;

/**
 * @author glorydark
 */
public class ResetSpeedCommand extends EasySubCommand {

    public ResetSpeedCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        PlayerTools.resetSpeed(commandSender.asPlayer(), 1f);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
