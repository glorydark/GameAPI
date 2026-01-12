package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.BlockTools;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditFillCommand extends EasySubCommand {

    public WorldEditFillCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[] {
                CommandParameter.newType("blockId", false, CommandParamType.STRING),
                CommandParameter.newEnum("replaceExistedBlock", true, CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isPlayer()) {
            Player player = commandSender.asPlayer();
            if (!WorldEditCommand.isTwoPosHasUndefined(player)) {
                PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
                if (args.length > 0) {
                    Block fillFiller = BlockTools.getBlockfromString(args[0]);
                    if (fillFiller == null) {
                        commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + args[0]);
                        return false;
                    }
                    WorldEditTools.fill(player,
                            posSet.getPos1(),
                            posSet.getPos2(),
                            fillFiller,
                            args.length != 2 || Boolean.parseBoolean(args[1]));
                    return false;
                }
            }
        }
        if (args.length >= 6) {
            Vector3 pos1 = new Vector3(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            Vector3 pos2 = new Vector3(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
            Level level;
            if (commandSender.isPlayer()) {
                Player player = commandSender.asPlayer();
                WorldEditTools.fill(player,
                        pos1,
                        pos2,
                        BlockTools.getBlockfromString(args[6]),
                        true);
            } else {
                level = Server.getInstance().getLevelByName(args[6]);
                WorldEditTools.fill(commandSender,
                        pos1,
                        pos2,
                        level,
                        BlockTools.getBlockfromString(args[7]),
                        true);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
