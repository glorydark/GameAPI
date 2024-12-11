package gameapi.commands.worldedit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import gameapi.commands.base.EasySubCommand;
import gameapi.commands.worldedit.WorldEditCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditSaveBuildCommand extends EasySubCommand {

    public WorldEditSaveBuildCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isTwoPosHasUndefined(player)) {
            return false;
        }
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
        // /gameapi savebuild 631 71 -256
        Vector3 p1 = posSet.getPos1();
        Vector3 p2 = posSet.getPos2();
        WorldEditTools.saveBuild(player, p1, p2, player.getLevel());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}