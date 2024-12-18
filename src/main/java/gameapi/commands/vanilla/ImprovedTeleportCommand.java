package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SpatialTools;

/**
 * @author glorydark
 */
public class ImprovedTeleportCommand extends EasySubCommand {

    public ImprovedTeleportCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 4) {
            return false;
        }
        Player player = Server.getInstance().getPlayer(args[0]);
        if (player == null) {
            return false;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i]).append(":");
        }
        Location location = SpatialTools.parseLocation(str.toString()).getLocation();
        player.teleport(location);
        return false;
    }
}