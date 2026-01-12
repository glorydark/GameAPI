package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditPreviewBuildCommand extends EasySubCommand {

    public WorldEditPreviewBuildCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[] {
                CommandParameter.newType("buildFileName", false, CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isFirstPosSet(player)) {
            PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
            WorldEditTools.previewBuild(player, args[0], posSet.getPos1());
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}