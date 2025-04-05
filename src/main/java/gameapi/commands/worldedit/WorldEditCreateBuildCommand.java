package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditCreateBuildCommand extends EasySubCommand {

    public WorldEditCreateBuildCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isFirstPosSet(player)) {
            PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
            WorldEditTools.generateBuild(player, args[0], posSet.getPos1());
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}