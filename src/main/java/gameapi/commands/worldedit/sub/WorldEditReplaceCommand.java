package gameapi.commands.worldedit.sub;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.commands.worldedit.WorldEditCommand;
import gameapi.tools.BlockTools;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditReplaceCommand extends EasySubCommand {

    public WorldEditReplaceCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isTwoPosHasUndefined(player)) {
            return false;
        }
        boolean checkDamage = args.length != 3 || Boolean.parseBoolean(args[2]);
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
        if (args.length >= 2) {
            Block sourceBlock = BlockTools.getBlockfromString(args[0]);
            Block targetBlock = BlockTools.getBlockfromString(args[1]);
            if (sourceBlock == null) {
                commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + args[0]);
                return false;
            }
            if (targetBlock == null) {
                commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + args[1]);
                return false;
            }
            WorldEditTools.replaceBlock(player, posSet.getPos1(), posSet.getPos2(), sourceBlock, targetBlock, checkDamage);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
