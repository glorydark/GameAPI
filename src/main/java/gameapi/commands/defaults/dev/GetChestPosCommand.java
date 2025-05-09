package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class GetChestPosCommand extends EasySubCommand {

    public GetChestPosCommand(String name) {
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
        Level l = p.getLevel();
        bb.forEach((i, i1, i2) -> {
            if (l.getBlock(i, i1, i2, true).getId() == BlockID.CHEST) {
                System.out.println(i + ", " + i1 + ", " + i2 + "\n");
            }
        });
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}
