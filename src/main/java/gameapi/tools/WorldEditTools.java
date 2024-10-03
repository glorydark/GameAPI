package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.task.BlockFillTask;
import gameapi.task.BlockReplaceTask;
import gameapi.utils.IntegerAxisAlignBB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author glorydark
 */
public class WorldEditTools {

    protected static boolean generatingLargeBuild = false;

    public static void fill(Location pos1, Location pos2, Block block, boolean isReplacedExistedBlock) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
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
            if (sender != null) {
                sender.sendMessage(TextFormat.RED + "Block is undefined!");
            }
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
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
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
        }
    }

    public static void createBall(Location centerPos, Block block, double radius, boolean fillInside) {
        if (!centerPos.isValid()) {
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        createBall(null, centerPos, centerPos.getLevel(), block, radius, fillInside);
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
        }
    }

    public static void replaceBlock(Location pos1, Location pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameAPI.getGameDebugManager().info(TextFormat.RED + "Center pos hasn't defined the level yet!");
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
            if (sender != null) {
                sender.sendMessage(TextFormat.RED + "Block is undefined!");
            }
        } else {
            BlockReplaceTask replaceTask = new BlockReplaceTask(level, sourceBlock, targetBlock, checkDamage);
            bb.forEach((i, i1, i2) -> replaceTask.addPos(new Vector3(i, i1, i2)));
            GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(replaceTask);
            if (sender != null) {
                sender.sendMessage(TextFormat.GREEN + "Already replace " + replaceTask.getImmutablePosList().size() + " blocks from " + sourceBlock.getName() + " to " + targetBlock.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), replaceTask.getEndMillis())));
            }
        }
    }

    @Internal
    public static void generateBuild(Player player, String fileName, Vector3 startPos) {
        generateBuild(player, fileName, startPos, player.getLevel());
    }

    @Internal
    public static void generateBuild(CommandSender sender, String fileName, Vector3 startPos, Level level) {
        if (generatingLargeBuild) {
            GameAPI.getInstance().getLogger().info("You have started a task of creating or saving build. Please wait...");
            return;
        }
        File[] files = new File(GameAPI.getPath() + "/buildings/" + fileName + "/").listFiles();
        if (files == null) {
            sender.sendMessage("Cannot find folder");
            return;
        }
        generatingLargeBuild = true;
        Position startPosition = Location.fromObject(startPos, level);
        long saveStartMillis = System.currentTimeMillis();
        int maxGenerateSections = files.length;
        AtomicInteger blockCount = new AtomicInteger();
        AtomicInteger generateSectionCount = new AtomicInteger();
        sender.sendMessage("Start generating building task... [Count: " + maxGenerateSections + "]");
        GameAPI.getInstance().getLogger().info("Start generating building task... [Count: " + maxGenerateSections + "]");
        try {
            CompletableFuture.runAsync(() -> {
                long startMillisForSection = System.currentTimeMillis();
                for (File file : files) {
                    CompoundTag compoundTag;
                    try {
                        compoundTag = NBTIO.read(file);
                    } catch (IOException e) {
                        GameAPI.getInstance().getLogger().error(e.toString());
                        return;
                    }
                    List<CompoundTag> tags = compoundTag.getList("blocks", CompoundTag.class).getAll();
                    long maxCount = tags.size();
                    AtomicLong lastTipPercentage = new AtomicLong(0);
                    AtomicLong generated = new AtomicLong(0);
                    for (CompoundTag blocks : tags) {
                        int x = startPosition.getFloorX() + blocks.getInt("x");
                        int y = startPosition.getFloorY() + blocks.getInt("y");
                        int z = startPosition.getFloorZ() + blocks.getInt("z");
                        if (y > 256 || y < 0) {
                            System.out.println("Out of world's bound!");
                            continue;
                        }
                        Block specificBlock = Block.get(blocks.getInt("blockId"), blocks.getInt("damage"));
                        if (specificBlock == null) {
                            specificBlock = new BlockUnknown(blocks.getInt("blockId"), blocks.getInt("damage"));
                        }
                        if (specificBlock.getId() != BlockID.AIR) {
                            Vector3 pos = new Vector3(x, y, z);
                            level.setBlock(pos, specificBlock, true, false);
                            if (generated.get() >= (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                GameAPI.getInstance().getLogger().info("[" + blockCount + "] Generating block... §e" + lastTipPercentage.get() + "%");
                                lastTipPercentage.getAndAdd(5);
                            }
                            CompoundTag layer1 = blocks.getCompound("layer1");
                            int blockLayer1Id = layer1.getInt("blockId");
                            int blockLayer1Damage = layer1.getInt("damage");
                            if (blockLayer1Id != 0) {
                                level.setBlock(pos, 1, Block.get(blockLayer1Id, blockLayer1Damage), true, false);
                            }
                        }
                        // GameAPI.plugin.getLogger().info("Generating block info at {" + x + ", " + y + ", " + z + "} with {" + block.getId() + ":" + block.getDamage() + "}");
                        generated.getAndIncrement();
                        blockCount.getAndIncrement();
                    }
                    generateSectionCount.getAndIncrement();
                    String timeDiffToString = SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForSection);
                    sender.sendMessage("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                    GameAPI.getInstance().getLogger().info("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                }
            }).exceptionally(throwable -> {
                GameAPI.getInstance().getLogger().error(throwable.toString());
                return null;
            }).thenRun(() -> {
                generatingLargeBuild = false;
                sender.sendMessage("Finish all saving tasks! Section Count: " + generateSectionCount + ". Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), saveStartMillis));
            });
        } catch (CompletionException e) {
            GameAPI.getInstance().getLogger().error(e.toString());
        }
    }

    public static void generateBuild(String fileName, Vector3 startPos, Level level) {
        File[] files = new File(GameAPI.getPath() + "/buildings/" + fileName + "/").listFiles();
        if (files == null) {
            GameAPI.getInstance().getLogger().error("Cannot find folder");
            return;
        }
        generatingLargeBuild = true;
        Position startPosition = Location.fromObject(startPos, level);
        AtomicInteger blockCount = new AtomicInteger();
        AtomicInteger generateSectionCount = new AtomicInteger();
        try {
            CompletableFuture.runAsync(() -> {
                for (File file : files) {
                    CompoundTag compoundTag;
                    try {
                        compoundTag = NBTIO.read(file);
                    } catch (IOException e) {
                        GameAPI.getInstance().getLogger().error(e.toString());
                        return;
                    }
                    List<CompoundTag> tags = compoundTag.getList("blocks", CompoundTag.class).getAll();
                    long maxCount = tags.size();
                    AtomicLong lastTipPercentage = new AtomicLong(0);
                    AtomicLong generated = new AtomicLong(0);
                    for (CompoundTag blocks : tags) {
                        int x = startPosition.getFloorX() + blocks.getInt("x");
                        int y = startPosition.getFloorY() + blocks.getInt("y");
                        int z = startPosition.getFloorZ() + blocks.getInt("z");
                        if (y > 256 || y < 0) {
                            continue;
                        }
                        Block specificBlock = Block.get(blocks.getInt("blockId"), blocks.getInt("damage"));
                        if (specificBlock == null) {
                            specificBlock = new BlockUnknown(blocks.getInt("blockId"), blocks.getInt("damage"));
                        }
                        if (specificBlock.getId() != BlockID.AIR) {
                            Vector3 pos = new Vector3(x, y, z);
                            level.setBlock(pos, specificBlock, true, false);
                            if (generated.get() >= (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                lastTipPercentage.getAndAdd(5);
                            }
                            CompoundTag layer1 = blocks.getCompound("layer1");
                            int blockLayer1Id = layer1.getInt("blockId");
                            int blockLayer1Damage = layer1.getInt("damage");
                            if (blockLayer1Id != 0) {
                                level.setBlock(pos, 1, Block.get(blockLayer1Id, blockLayer1Damage), true, false);
                            }
                        }
                        generated.getAndIncrement();
                        blockCount.getAndIncrement();
                    }
                    generateSectionCount.getAndIncrement();
                }
            }).exceptionally(throwable -> {
                GameAPI.getInstance().getLogger().error(throwable.toString());
                return null;
            });
        } catch (CompletionException e) {
            GameAPI.getInstance().getLogger().error(e.toString());
        }
    }

    @Internal
    public static void saveBuild(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level) {
        saveBuild(sender,
                new Vector3(Math.min(pos1.getFloorX(), pos2.getFloorX()),
                        Math.min(pos1.getFloorY(), pos2.getFloorY()),
                        Math.min(pos1.getFloorZ(), pos2.getFloorZ())),
                pos1, pos2, level);
    }

    @Internal
    public static void saveBuild(CommandSender sender, Vector3 centerPos, Vector3 pos1, Vector3 pos2, Level level) {
        if (generatingLargeBuild) {
            GameAPI.getInstance().getLogger().info("You have started a task of creating or saving build. Please wait...");
            return;
        }
        generatingLargeBuild = true;
        IntegerAxisAlignBB integerAxisAlignBB = new IntegerAxisAlignBB(pos1, pos2);
        String name = String.valueOf(System.currentTimeMillis());
        long saveStartMillis = System.currentTimeMillis();

        IntegerAxisAlignBB[] bbs = integerAxisAlignBB.splitAABB(64, 64, 64);
        GameAPI.getGameDebugManager().info("Start building save task in {" + integerAxisAlignBB + "}");
        sender.sendMessage("Start building save task in {" + integerAxisAlignBB + "}");

        sender.sendMessage("For better performance, the range are cut into various sections: ");
        GameAPI.getInstance().getLogger().info("For better performance, the range are cut into various sections: ");
        for (int i = 0; i < bbs.length; i++) {
            sender.sendMessage("- [" + i + "] " + bbs[i].toString());
            GameAPI.getInstance().getLogger().info("- [" + i + "] " + bbs[i].toString());
        }

        AtomicLong readBlockCountAll = new AtomicLong(0);
        AtomicInteger sectionCount = new AtomicInteger(0);
        CompletableFuture.runAsync(() -> {
            for (int bbsIndex = 0; bbsIndex < bbs.length; bbsIndex++) {
                IntegerAxisAlignBB newBB = bbs[bbsIndex];
                sender.sendMessage("Starting task " + bbsIndex + "...");
                long startMillisForSection = System.currentTimeMillis();
                // Based on them to check whether it's finished or not
                AtomicLong queryBlockTimes = new AtomicLong(0);
                long maxCount = newBB.getSize();
                AtomicLong lastTipPercentage = new AtomicLong(0);
                AtomicLong readBlockCountForSection = new AtomicLong(0);
                CompoundTag tag = new CompoundTag().putList(new ListTag<>("blocks"));
                int finalBbsIndex = bbsIndex;
                newBB.forEach(((i, i1, i2) -> {
                    Position position = new Position(i, i1, i2, level);
                    if (!position.getChunk().isLoaded()) {
                        try {
                            position.getChunk().load(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Block blockAtPosition = level.getBlock(position, true);
                    if (blockAtPosition.getId() != Block.AIR) {
                        int x = i - centerPos.getFloorX();
                        int y = i1 - centerPos.getFloorY();
                        int z = i2 - centerPos.getFloorZ();
                        CompoundTag addTag = new CompoundTag()
                                .putInt("x", x)
                                .putInt("y", y)
                                .putInt("z", z)
                                .putInt("blockId", blockAtPosition.getId())
                                .putInt("damage", blockAtPosition.getDamage());
                        Block blockLayer1 = level.getBlock(position, 1, true);
                        if (blockLayer1.getId() != BlockID.AIR) {
                            addTag.putCompound("layer1", new CompoundTag()
                                    .putInt("blockId", blockLayer1.getId())
                                    .putInt("damage", blockLayer1.getDamage())
                            );
                        }
                        tag.getList("blocks", CompoundTag.class).add(addTag);
                        readBlockCountAll.getAndIncrement();
                        readBlockCountForSection.getAndIncrement();
                    }
                    queryBlockTimes.getAndIncrement();
                    if (queryBlockTimes.get() > (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                        GameAPI.getInstance().getLogger().info("[" + finalBbsIndex + "] Saving sections... §e" + lastTipPercentage + "%");
                        lastTipPercentage.getAndAdd(5);
                    }
                    if (queryBlockTimes.get() >= newBB.getSize()) {
                        if (readBlockCountForSection.get() != 0) {
                            new File(GameAPI.getPath() + "/buildings/" + name + "/").mkdirs();
                            File file = new File(GameAPI.getPath() + "/buildings/" + name + "/" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".nbt");
                            if (file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                                fileOutputStream.write(NBTIO.write(tag));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            sectionCount.getAndIncrement();
                            StringBuilder builder = new StringBuilder();
                            builder.append("[").append(finalBbsIndex).append("] ")
                                    .append("Finish saving building in ")
                                    .append(name)
                                    .append(" | ")
                                    .append("Time Cost: ").append(SmartTools.timeDiffMillisToString(startMillisForSection, System.currentTimeMillis()));
                            sender.sendMessage(builder.toString());
                            GameAPI.getInstance().getLogger().info(builder.toString());
                        } else {
                            StringBuilder builder = new StringBuilder();
                            builder.append("[").append(finalBbsIndex).append("] ")
                                    .append(" Finish reading the section. The section contains nothing but air blocks. Turning into next sections...");
                            sender.sendMessage(builder.toString());
                            GameAPI.getInstance().getLogger().info(builder.toString());
                        }
                    }
                }));
            }
        }, GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR).thenRun(() -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Finish generating building task! ")
                            .append("Section count: ").append(sectionCount.get())
                            .append(" | ")
                            .append("Block count: ").append(readBlockCountAll.get())
                            .append(" | ")
                            .append("Time cost: ").append(SmartTools.timeDiffMillisToString(System.currentTimeMillis(), saveStartMillis));
                    sender.sendMessage(builder.toString());
                    GameAPI.getInstance().getLogger().info(builder.toString());
                    generatingLargeBuild = false;
                }
        );
    }

    public static boolean isGeneratingLargeBuild() {
        return generatingLargeBuild;
    }
}
