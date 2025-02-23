package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import it.unimi.dsi.fastutil.ints.IntSet;

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
        player.getChunk().getProvider().requestChunkTask(IntSet.of(player.protocol), player.getChunkX(), player.getChunkZ());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
