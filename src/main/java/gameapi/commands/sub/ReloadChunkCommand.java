package gameapi.commands.sub;

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
        player.getLevel().requestChunk(player.getChunkX(), player.getChunkZ(), player);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
