package gameapi.commands.defaults.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.weather.EntityLightning;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class ThunderCommand extends EasySubCommand {

    public ThunderCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        EntityLightning entityLightning = new EntityLightning(player.getChunk(), EntityLightning.getDefaultNBT(player));
        entityLightning.spawnToAll();
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
