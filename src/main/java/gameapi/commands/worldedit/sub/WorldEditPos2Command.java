package gameapi.commands.worldedit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.commands.worldedit.WorldEditCommand;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditPos2Command extends EasySubCommand {

    public WorldEditPos2Command(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
        }
        WorldEditCommand.posSetLinkedHashMap.get(player).setPos2(player.getLocation());
        player.sendMessage(TextFormat.GREEN + "Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
