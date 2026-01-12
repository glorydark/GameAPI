package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditPreviewBuildAreaCommand extends EasySubCommand {

    public WorldEditPreviewBuildAreaCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[0]);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (WorldEditCommand.isTwoPosHasUndefined(player)) {
            PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);

            WorldEditTools.previewBuild(player, posSet.getPos1(), posSet.getPos2());
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}