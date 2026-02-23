package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;
import gameapi.utils.RotationType;

import java.util.Locale;

/**
 * @author glorydark
 */
public class WorldEditCreateBuildCommand extends EasySubCommand {

    public WorldEditCreateBuildCommand(String name) {
        super(name);

        this.commandParameters.put("default", new CommandParameter[] {
                CommandParameter.newType("buildFileName", false, CommandParamType.STRING),
                CommandParameter.newEnum("rotDegree", true, new String[]{"0", "90", "180", "270"}),
                CommandParameter.newEnum("rotType", true, new String[]{"none", "around_center", "around_start_position"})
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isFirstPosSet(player)) {
            PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
            RotationType rotationType = RotationType.AROUND_CENTER;
            try {
                if (args.length == 3) {
                    rotationType = RotationType.valueOf(args[2].toUpperCase(Locale.ROOT));
                }
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage("Cannot find rotation type: " + args[2]);
                return false;
            }
            int rotDegree = args.length >= 2? Integer.parseInt(args[1]) : 0;
            commandSender.sendMessage("Set Rotation Type to " + rotationType.name().toLowerCase(Locale.ROOT));
            WorldEditTools.generateBuild(player, args[0], posSet.getPos1(), player.getLevel(), rotDegree, rotationType);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}