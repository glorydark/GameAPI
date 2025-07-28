package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.SimpleAxisAlignedBB;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class GetBlockSpaceCommand extends EasySubCommand {

    public GetBlockSpaceCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player p = commandSender.asPlayer();
        if (p == null) {
            return false;
        }
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(p);
        if (posSet == null) {
            p.sendMessage("Pos set is null");
            return false;
        }
        if (posSet.getPos1() == null || posSet.getPos2() == null) {
            p.sendMessage("You haven't set pos1 or pos2");
            return false;
        }
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
        commandSender.sendMessage("xDist: " + (bb.getMaxX() - bb.getMinX() + 1));
        commandSender.sendMessage("yDist: " + (bb.getMaxY() - bb.getMinY() + 1));
        commandSender.sendMessage("zDist: " + (bb.getMaxZ() - bb.getMinZ() + 1));
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}
