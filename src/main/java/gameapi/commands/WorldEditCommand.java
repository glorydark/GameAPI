package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;
import gameapi.tools.BlockTools;
import gameapi.tools.SchematicConverter;
import gameapi.tools.WorldEditTools;
import gameapi.utils.PosSet;

import java.util.LinkedHashMap;

/**
 * @author glorydark
 */
@Experimental
public class WorldEditCommand extends Command {

    public static LinkedHashMap<Player, PosSet> posSetLinkedHashMap = new LinkedHashMap<>();

    public WorldEditCommand(String name) {
        super(name);
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isPlayer()) {
            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
            return false;
        } else if (!commandSender.isOp()) {
            return false;
        }
        final Player player = (Player) commandSender;
        if (strings.length > 0) {
            PosSet posSet = posSetLinkedHashMap.get(player);
            switch (strings[0].toLowerCase()) {
                case "true":
                    GameAPI.worldEditPlayers.add((Player) commandSender);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.on"));
                    if (strings.length != 2) {
                        return false;
                    }
                case "false":
                    GameAPI.worldEditPlayers.remove((Player) commandSender);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.off"));
                case "pos1":
                    if (!posSetLinkedHashMap.containsKey(player)) {
                        posSetLinkedHashMap.put(player, new PosSet());
                    }
                    posSetLinkedHashMap.get(player).setPos1(player.getLocation());
                    player.sendMessage(TextFormat.GREEN + "Successfully set pos1 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "pos2":
                    if (!posSetLinkedHashMap.containsKey(player)) {
                        posSetLinkedHashMap.put(player, new PosSet());
                    }
                    posSetLinkedHashMap.get(player).setPos2(player.getLocation());
                    player.sendMessage(TextFormat.GREEN + "Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "circle":
                    if (strings.length < 3) {
                        return false;
                    }
                    Block circleFiller = BlockTools.getBlockfromString(strings[1]);
                    if (circleFiller == null) {
                        commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + strings[1]);
                        return false;
                    } else {
                        if (this.isFirstPosSet(player)) {
                            WorldEditTools.createCircle(player,
                                    posSet.getPos1(),
                                    circleFiller,
                                    Double.parseDouble(strings[2]),
                                    strings.length != 4 || Boolean.parseBoolean(strings[3]));
                        } else {
                            player.sendMessage(TextFormat.RED + "Pos 1 is undefined!");
                        }
                    }
                    break;
                case "ball":
                    if (strings.length < 3) {
                        return false;
                    }
                    Block ballFiller = BlockTools.getBlockfromString(strings[1]);
                    if (ballFiller == null) {
                        commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + strings[1]);
                        return false;
                    } else {
                        if (this.isFirstPosSet(player)) {
                            WorldEditTools.createBall(player,
                                    posSet.getPos1(),
                                    ballFiller,
                                    Double.parseDouble(strings[2]),
                                    strings.length != 4 || Boolean.parseBoolean(strings[3]));
                        } else {
                            player.sendMessage(TextFormat.RED + "Pos 1 is undefined!");
                        }
                    }
                    break;
                case "fill": // fill blockId blockMeta
                    if (this.isTwoPosHasUndefined(player)) {
                        return false;
                    }
                    if (strings.length >= 2) {
                        Block fillFiller = BlockTools.getBlockfromString(strings[1]);
                        if (fillFiller == null) {
                            commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        WorldEditTools.fill(player,
                                posSet.getPos1(),
                                posSet.getPos2(),
                                fillFiller,
                                strings.length != 3 || Boolean.parseBoolean(strings[2]));
                    }
                    break;
                case "replace":
                    if (this.isTwoPosHasUndefined(player)) {
                        return false;
                    }
                    boolean checkDamage = strings.length != 4 || Boolean.parseBoolean(strings[3]);
                    posSet = posSetLinkedHashMap.get(player);
                    if (strings.length >= 3) {
                        Block sourceBlock = BlockTools.getBlockfromString(strings[1]);
                        Block targetBlock = BlockTools.getBlockfromString(strings[2]);
                        if (sourceBlock == null) {
                            commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        if (targetBlock == null) {
                            commandSender.sendMessage(TextFormat.RED + "Unable to find the block identifier: " + strings[2]);
                            return false;
                        }
                        WorldEditTools.replaceBlock(player, posSet.getPos1(), posSet.getPos2(), sourceBlock, targetBlock, checkDamage);
                    }
                    break;
                case "clearpos":
                    posSetLinkedHashMap.remove(player);
                    player.sendMessage(TextFormat.GREEN + "Your pos set has been cleared successfully!");
                    break;
                case "resetc":
                    player.getLevel().regenerateChunk(player.getChunkX(), player.getChunkZ());
                    player.sendMessage("Reset chunk successfully!");
                    break;
                case "savebuild":
                    if (this.isTwoPosHasUndefined(player)) {
                        return false;
                    }
                    // /gameapi savebuild 631 71 -256
                    Vector3 p1 = posSet.getPos1();
                    Vector3 p2 = posSet.getPos2();
                    WorldEditTools.saveBuild(player, p1, p2, player.getLevel());
                    break;
                case "createbuild":
                    if (this.isFirstPosSet(player)) {
                        WorldEditTools.generateBuild(player, strings[1], posSet.getPos1());
                    }
                    break;
                case "loadschematics":
                    SchematicConverter.createBuildFromSchematic(player, strings[1]);
                    break;
            }
        }
        return false;
    }

    public boolean isTwoPosHasUndefined(Player player) {
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

    public boolean isFirstPosSet(Player player) {
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
