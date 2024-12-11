
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
public class WorldEditBallCommand extends EasySubCommand {

    public WorldEditBallCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 2) {
            return false;
        }
        Player player = commandSender.asPlayer();
        Block ballFiller = BlockTools.getBlockfromString(args[0]);
        if (ballFiller == null) {
            commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + args[0]);
            return false;
        } else {
            if (WorldEditCommand.isFirstPosSet(player)) {
                PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
                WorldEditTools.createBall(player,
                        posSet.getPos1(),
                        ballFiller,
                        Double.parseDouble(args[1]),
                        args.length != 3 || Boolean.parseBoolean(args[2]));
            } else {
                player.sendMessage(TextFormat.RED + "Pos 1 is undefined!");
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}