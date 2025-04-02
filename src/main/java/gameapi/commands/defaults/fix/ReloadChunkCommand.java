package gameapi.commands.defaults.fix;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class ReloadChunkCommand extends EasySubCommand {

    public ReloadChunkCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (player == null) {
            return false;
        }
        int requestRadius = 1;
        int chunkX = player.getChunkX();
        int chunkZ = player.getChunkZ();
        for (int x = chunkX - requestRadius; x <= chunkX + requestRadius; x++) {
            for (int z = chunkZ - requestRadius; z <= chunkZ + requestRadius; z++) {
                player.getLevel().requestChunk(x, z, player);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
