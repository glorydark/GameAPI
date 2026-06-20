package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

/**
 * @author glorydark
 */
public class WorldEditSaveBuildCommand extends EasySubCommand {

    public WorldEditSaveBuildCommand(String name) {
        super(name);

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[0]);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = asPlayer(commandSender);
        if (WorldEditCommand.isTwoPosHasUndefined(player)) {
            return false;
        }
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
        // /gameapi savebuild 631 71 -256
        Vector3 p1 = posSet.getPos1();
        Vector3 p2 = posSet.getPos2();

        CompoundTag extra = WorldEditCommand.buildExtraTagFromCache(player);
        if (extra != null) {
            Vector3 minPos = new Vector3(
                    Math.min(p1.getFloorX(), p2.getFloorX()),
                    Math.min(p1.getFloorY(), p2.getFloorY()),
                    Math.min(p1.getFloorZ(), p2.getFloorZ())
            );
            extra = WorldEditTools.relativizeExtraTag(extra, minPos,
                    WorldEditCommand.extraTagCache.getOrDefault(player, new java.util.LinkedHashMap<>()).keySet()
                            .toArray(new String[0]));
        }

        WorldEditTools.saveBuild(player, p1, p2, player.getLevel(), extra);
        WorldEditCommand.clearExtraTagCache(player);
        player.sendMessage(TextFormat.GRAY + "标记点缓存已清空");
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}