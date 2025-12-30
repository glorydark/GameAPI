package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.BlockTools;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditSpiralCommand extends EasySubCommand {

    public WorldEditSpiralCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 2) {
            return false;
        }
        Player player = commandSender.asPlayer();
        Block ballFiller = BlockTools.getBlockfromString(args[0]);
        int times = args.length == 2? Integer.parseInt(args[1]) : 3;
        if (ballFiller == null) {
            commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + args[0]);
            return false;
        } else {
            if (WorldEditCommand.isFirstPosSet(player)) {
                PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
                Level level = player.getLevel();
                Position position = posSet.getPos1();

                for (int i = 0; i < times; i++) {
                    WorldEditTools.generateSpiralRing(
                            level,
                            position.getFloorX(),     // cx
                            position.getFloorY() + 20 * i,      // baseY
                            position.getFloorZ(),     // cz
                            30,       // 半径
                            12,       // 厚度
                            20,      // 螺旋高度
                            5,
                            Block.get(Block.STONE)
                    );
                }
                WorldEditTools.buildCylinder(
                        level,
                        position,
                        times * 20 + 5,
                        18,
                        Block.get(Block.STONE));
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