package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.annotation.Experimental;
import gameapi.commands.base.EasyCommand;
import gameapi.commands.worldedit.*;
import gameapi.utils.PosSet;

import java.util.*;

/**
 * @author glorydark
 */
@Experimental
public class WorldEditCommand extends EasyCommand {

    public static LinkedHashMap<Player, PosSet> posSetLinkedHashMap = new LinkedHashMap<>();

    public static Map<Player, Map<String, Vector3>> extraTagCache = new HashMap<>();

    public static Map<Player, Vector3> pendingExtraPosition = new HashMap<>();

    public static CompoundTag buildExtraTagFromCache(Player player) {
        Map<String, Vector3> points = extraTagCache.get(player);
        if (points == null || points.isEmpty()) return null;
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, Vector3> entry : points.entrySet()) {
            ListTag<IntTag> list = new ListTag<>(entry.getKey());
            list.add(new IntTag("", entry.getValue().getFloorX()));
            list.add(new IntTag("", entry.getValue().getFloorY()));
            list.add(new IntTag("", entry.getValue().getFloorZ()));
            tag.put(entry.getKey(), list);
        }
        return tag;
    }

    public static void clearExtraTagCache(Player player) {
        extraTagCache.remove(player);
    }

    public WorldEditCommand(String name) {
        super(name);
        this.commandParameters.clear();

        this.registerCommand(new WorldEditDebugCommand("debug"));
        this.registerCommand(new WorldEditPos1Command("pos1"));
        this.registerCommand(new WorldEditPos2Command("pos2"));
        this.registerCommand(new WorldEditClearPosCommand("clearpos"));

        this.registerCommand(new WorldEditFillCommand("fill"));
        this.registerCommand(new WorldEditBallCommand("ball"));
        this.registerCommand(new WorldEditHalfBallCommand("halfball"));
        this.registerCommand(new WorldEditCircleCommand("circle"));
        this.registerCommand(new WorldEditReplaceCommand("replace"));

        this.registerCommand(new WorldEditSaveBuildCommand("savebuild"));
        this.registerCommand(new WorldEditCreateBuildCommand("createbuild"));
        this.registerCommand(new WorldEditPreviewBuildCommand("previewbuild"));
        this.registerCommand(new WorldEditPreviewBuildAreaCommand("previewbuildarea"));

        this.registerCommand(new WorldEditResetChunkCommand("resetc"));
        this.registerCommand(new WorldEditBWTestCommand("bwtest"));

        this.registerCommand(new WorldEditFindCommand("findblock"));
        this.registerCommand(new WorldEditScanNewBlockCommand("scannew"));

        this.registerCommand(new WorldEditSpiralCommand("spiral"));

        this.registerCommand(new WorldEditSchematicCommand("loadschema"));
    }

    public static boolean isTwoPosHasUndefined(Player player) {
        PosSet posSet = posSetLinkedHashMap.get(player);
        if (posSet == null) {
            player.sendMessage("Pos set is null");
            return true;
        }
        if (posSet.getPos1() == null || posSet.getPos2() == null) {
            player.sendMessage("You haven't set pos1 or pos2");
            return true;
        }
        return false;
    }

    public static boolean isFirstPosSet(Player player) {
        PosSet posSet = posSetLinkedHashMap.get(player);
        if (posSet == null) {
            player.sendMessage("Pos set is null");
            return false;
        }
        if (posSet.getPos1() == null) {
            player.sendMessage("You haven't set pos1");
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}
