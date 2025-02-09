package gameapi.commands.worldedit;

import cn.nukkit.Player;
import gameapi.annotation.Experimental;
import gameapi.commands.base.EasyCommand;
import gameapi.commands.worldedit.sub.*;
import gameapi.utils.PosSet;

import java.util.LinkedHashMap;

/**
 * @author glorydark
 */
@Experimental
public class WorldEditCommand extends EasyCommand {

    public static LinkedHashMap<Player, PosSet> posSetLinkedHashMap = new LinkedHashMap<>();

    public WorldEditCommand(String name) {
        super(name);
        this.commandParameters.clear();

        this.registerCommand(new WorldEditDebugCommand("debug"));
        this.registerCommand(new WorldEditPos1Command("pos1"));
        this.registerCommand(new WorldEditPos2Command("pos2"));
        this.registerCommand(new WorldEditClearPosCommand("clearpos"));

        this.registerCommand(new WorldEditFillCommand("fill"));
        this.registerCommand(new WorldEditBallCommand("ball"));
        this.registerCommand(new WorldEditCircleCommand("circle"));
        this.registerCommand(new WorldEditReplaceCommand("replace"));

        this.registerCommand(new WorldEditSaveBuildCommand("savebuild"));
        this.registerCommand(new WorldEditCreateBuildCommand("createbuild"));

        this.registerCommand(new WorldEditResetChunkCommand("resetc"));
        this.registerCommand(new WorldEditBWTestCommand("bwtest"));

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
}
