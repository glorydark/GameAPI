package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditPos2Command extends EasySubCommand {

    public WorldEditPos2Command(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[0]);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = asPlayer(commandSender);
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
