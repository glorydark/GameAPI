package gameapi.commands.worldedit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class WorldEditResetChunkCommand extends EasySubCommand {

    public WorldEditResetChunkCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        player.getLevel().regenerateChunk(player.getChunkX(), player.getChunkZ());
        player.sendMessage("Reset chunk successfully!");
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
