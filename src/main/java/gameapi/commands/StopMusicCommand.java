package gameapi.commands;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.VanillaCustomMusicTools;

/**
 * @author Glorydark
 * For in-game test
 */
public class StopMusicCommand extends EasySubCommand {

    public StopMusicCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        VanillaCustomMusicTools.stopCustomMusic(0f, commandSender.asPlayer());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
