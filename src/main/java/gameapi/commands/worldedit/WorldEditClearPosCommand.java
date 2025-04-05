package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class WorldEditClearPosCommand extends EasySubCommand {

    public WorldEditClearPosCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        WorldEditCommand.posSetLinkedHashMap.remove(player);
        player.sendMessage(TextFormat.GREEN + "Your pos set has been cleared successfully!");
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
