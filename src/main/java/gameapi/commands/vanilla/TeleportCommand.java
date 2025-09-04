package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Location;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SpatialTools;

/**
 * @author glorydark
 */
public class TeleportCommand extends EasySubCommand {

    public TeleportCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.STRING),
                CommandParameter.newType("x", false, CommandParamType.FLOAT),
                CommandParameter.newType("y", false, CommandParamType.FLOAT),
                CommandParameter.newType("z", false, CommandParamType.FLOAT),
                CommandParameter.newType("level", true, CommandParamType.STRING),
                CommandParameter.newType("yaw", true, CommandParamType.FLOAT),
                CommandParameter.newType("pitch", true, CommandParamType.FLOAT),
                CommandParameter.newType("headyaw", true, CommandParamType.FLOAT)
        });
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
