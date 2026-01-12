package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.BlockTools;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditReplaceCommand extends EasySubCommand {

    public WorldEditReplaceCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[] {
                CommandParameter.newType("originalBlockId", false, CommandParamType.STRING),
                CommandParameter.newType("targetBlockId", false, CommandParamType.STRING),
                CommandParameter.newEnum("checkDamage", true, CommandEnum.ENUM_BOOLEAN)
        });
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
