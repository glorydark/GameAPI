package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class WorldEditResetChunkCommand extends EasySubCommand {

    public WorldEditResetChunkCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[0]);
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
