package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.manager.GameDebugManager;
import gameapi.task.BlockFillTask;
import gameapi.task.BlockReplaceTask;

/**
 * @author glorydark
 */
public class WorldEditTools {

    public static void fill(Location pos1, Location pos2, Block block, boolean isReplacedExistedBlock) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameDebugManager.error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        fill(null, pos1, pos2, pos1.getLevel(), block, isReplacedExistedBlock);
    }

    public static void fill(Player player, Vector3 pos1, Vector3 pos2, Block block, boolean isReplacedExistedBlock) {
        fill(player, pos1, pos2, player.getLevel(), block, isReplacedExistedBlock);
    }

    public static void fill(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, Block block, boolean isReplacedExistedBlock) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        if (block == null) {
            sender.sendMessage(TextFormat.RED + "Block is undefined!");
        } else {
            BlockFillTask fillTask = new BlockFillTask(level, block);
            bb.forEach((i, i1, i2) -> {
                Vector3 pos = new Vector3(i, i1, i2);
                if (!isReplacedExistedBlock && level.getBlock(pos) != null) {
                    return;
                }
                fillTask.addPos(pos);
            });
            GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        }
    }

    public static void createCircle(Location centerPos, Block block, double radius, boolean fillInside) {
        if (!centerPos.isValid()) {
            GameDebugManager.error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        createCircle(null, centerPos, centerPos.getLevel(), block, radius, fillInside);
    }

    public static void createCircle(Player player, Vector3 centerPos, Block block, double radius, boolean fillInside) {
        createCircle(player, centerPos, player.getLevel(), block, radius, fillInside);
    }

    public static void createCircle(CommandSender sender, Vector3 centerPos, Level level, Block block, double radius, boolean fillInside) {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(centerPos, centerPos);
        bb = bb.expand(radius, 0, radius);
        BlockFillTask fillTask = new BlockFillTask(level, block);
        bb.forEach((i, i1, i2) -> {
            Vector3 pos = new Vector3(i, i1, i2);
            if (pos.distance(centerPos) <= radius) {
                if (!fillInside && pos.distance(centerPos) <= radius - 1) {
                    return;
                }
                fillTask.addPos(pos);
            }
        });
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        if (sender != null) {
            sender.sendMessage(TextFormat.GREEN + "Already create a ball with " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        } else {
            GameDebugManager.info(TextFormat.GREEN + "Already create a ball with " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        }
    }

    public static void createBall(Location centerPos, Block block, double radius, boolean fillInside) {
        if (!centerPos.isValid()) {
            GameDebugManager.error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        createCircle(null, centerPos, centerPos.getLevel(), block, radius, fillInside);
    }

    public static void createBall(Player player, Vector3 centerPos, Block block, double radius, boolean fillInside) {
        createBall(player, centerPos, player.getLevel(), block, radius, fillInside);
    }

    public static void createBall(CommandSender sender, Vector3 centerPos, Level level, Block block, double radius, boolean fillInside) {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(centerPos, centerPos);
        bb = bb.expand(radius, radius, radius);
        BlockFillTask fillTask = new BlockFillTask(level, block);
        bb.forEach((i, i1, i2) -> {
            Vector3 pos = new Vector3(i, i1, i2);
            if (pos.distance(centerPos) <= radius) {
                if (!fillInside && pos.distance(centerPos) <= radius - 1) {
                    return;
                }
                fillTask.addPos(pos);
            }
        });
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        if (sender != null) {
            sender.sendMessage(TextFormat.GREEN + "Already fill " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        } else {
            GameDebugManager.info(TextFormat.GREEN + "Already fill " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        }
    }

    public static void replaceBlock(Location pos1, Location pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameDebugManager.info(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        replaceBlock(null, pos1, pos2, pos1.getLevel(), sourceBlock, targetBlock, checkDamage);
    }

    public static void replaceBlock(Player player, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        replaceBlock(player, pos1, pos2, player.getLevel(), sourceBlock, targetBlock, checkDamage);
    }

    public static void replaceBlock(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        if (sourceBlock == null || targetBlock == null) {
            sender.sendMessage(TextFormat.RED + "Block is undefined!");
        } else {
            BlockReplaceTask replaceTask = new BlockReplaceTask(level, sourceBlock, targetBlock, checkDamage);
            bb.forEach((i, i1, i2) -> replaceTask.addPos(new Vector3(i, i1, i2)));
            GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(replaceTask);
            if (sender != null) {
                sender.sendMessage(TextFormat.GREEN + "Already replace " + replaceTask.getImmutablePosList().size() + " blocks from " + sourceBlock.getName() + " to " + targetBlock.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), replaceTask.getEndMillis())));
            } else {
                GameDebugManager.info(TextFormat.GREEN + "Already replace " + replaceTask.getImmutablePosList().size() + " blocks from " + sourceBlock.getName() + " to " + targetBlock.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), replaceTask.getEndMillis())));
            }
        }
    }
}
