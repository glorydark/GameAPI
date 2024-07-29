package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.AsyncTask;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;
import gameapi.tools.BlockTools;
import gameapi.tools.SchematicConverter;
import gameapi.tools.SmartTools;
import gameapi.utils.IntegerAxisAlignBB;
import gameapi.utils.PosSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author glorydark
 */
@Experimental
public class WorldEditCommand extends Command {

    public static ForkJoinPool THREAD_POOL_EXECUTOR;

    public static LinkedHashMap<Player, PosSet> posSetLinkedHashMap = new LinkedHashMap<>();

    public boolean generate;

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
                    player.sendMessage("Successfully set pos1 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "pos2":
                    if (!posSetLinkedHashMap.containsKey(player)) {
                        posSetLinkedHashMap.put(player, new PosSet());
                    }
                    posSetLinkedHashMap.get(player).setPos2(player.getLocation());
                    player.sendMessage("Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "fill": // fill blockId blockMeta
                    if (!isPosSet(player)) {
                        return false;
                    }
                    PosSet posSet = posSetLinkedHashMap.get(player);
                    if (strings.length == 2) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Block block = BlockTools.getBlockfromString(strings[1]);
                        if (block == null) {
                            commandSender.sendMessage("Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.getInstance(), new AsyncTask() {
                            @Override
                            public void onRun() {
                                //List<OperationEntry> simpleOperationEntries = new ArrayList<>();
                                bb.forEach(((i, i1, i2) -> {
                                    //Block before = player.getLevel().getBlock(i, i1, i2);
                                    /*
                                    simpleOperationEntries.add(SimpleOperationEntry.builder()
                                            .beforeBlockId(before.getId())
                                            .beforeBlockMeta(before.getDamage())
                                            .floorX(i)
                                            .floorY(i1)
                                            .floorZ(i2)
                                            .build());
                                     */
                                    player.getLevel().setBlock(i, i1, i2, block, true, false);
                                }));
                                player.sendMessage("Finish fill task");
                            }
                        });
                    }
                    break;
                case "replace":
                    if (!isPosSet(player)) {
                        return false;
                    }
                    posSet = posSetLinkedHashMap.get(player);
                    if (strings.length == 3) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Level level = player.getLevel();
                        Block block = BlockTools.getBlockfromString(strings[1]);
                        if (block == null) {
                            commandSender.sendMessage("Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        boolean checkBlockDamage = (strings[1].split(":").length == 2);
                        Block blockReplaced = BlockTools.getBlockfromString(strings[2]);
                        if (blockReplaced == null) {
                            commandSender.sendMessage("Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.getInstance(), new AsyncTask() {
                            @Override
                            public void onRun() {
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    Block before = player.getLevel().getBlock(i, i1, i2);
                                    if (before.getId() == block.getId()) {
                                        if (checkBlockDamage) {
                                            if (before.getDamage() == block.getDamage()) {
                                                return;
                                            }
                                        }
                                        level.setBlock(i, i1, i2, blockReplaced, true, true);
                                        count.getAndIncrement();
                                    }
                                }));
                                player.sendMessage("Finish fill task for " + count + " " + block.getName() + " replaced with " + blockReplaced.getName());
                            }
                        });
                    }
                    break;
                case "replaceunknown":
                    if (!isPosSet(player)) {
                        return false;
                    }
                    posSet = posSetLinkedHashMap.get(player);
                    if (strings.length == 2) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Level level = player.getLevel();
                        Block blockReplaced = BlockTools.getBlockfromString(strings[1]);
                        if (blockReplaced == null) {
                            commandSender.sendMessage("Unable to find the block identifier: " + strings[1]);
                            return false;
                        }
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.getInstance(), new AsyncTask() {
                            @Override
                            public void onRun() {
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    Block before = player.getLevel().getBlock(i, i1, i2);
                                    if (before instanceof BlockUnknown) {
                                        level.setBlock(i, i1, i2, blockReplaced, true, true);
                                        count.getAndIncrement();
                                    }
                                }));
                                player.sendMessage("Finish fill task for " + count + " unknown blocks replaced with" + blockReplaced.getName());
                            }
                        });
                    }
                    break;
                case "resetc":
                    player.getLevel().regenerateChunk(player.getChunkX(), player.getChunkZ());
                    player.sendMessage("Reset chunk successfully!");
                    break;
                case "savebuild":
                    // /gameapi savebuild 631 71 -256
                    if (generate) {
                        GameAPI.getInstance().getLogger().info("You have started a task of creating or saving build. Please wait...");
                    }
                    generate = true;
                    Vector3 p1 = new Vector3(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    Vector3 p2 = new Vector3(player.getFloorX(), player.getFloorY(), player.getFloorZ());
                    IntegerAxisAlignBB integerAxisAlignBB = new IntegerAxisAlignBB(p1, p2);
                    String name = String.valueOf(System.currentTimeMillis());
                    long startMillisForAll = System.currentTimeMillis();

                    IntegerAxisAlignBB[] bbs = integerAxisAlignBB.splitAABB(64, 64, 64);
                    GameAPI.getInstance().getLogger().info("Start building save task in {" + integerAxisAlignBB + "}");
                    player.sendMessage("Start building save task in {" + integerAxisAlignBB + "}");

                    player.sendMessage("For better performance, the range are cut into various sections: ");
                    GameAPI.getInstance().getLogger().info("For better performance, the range are cut into various sections: ");
                    for (int i = 0; i < bbs.length; i++) {
                        player.sendMessage("- [" + i + "] " + bbs[i].toString());
                        GameAPI.getInstance().getLogger().info("- [" + i + "] " + bbs[i].toString());
                    }

                    AtomicLong readBlockCountAll = new AtomicLong(0);
                    AtomicInteger sectionCount = new AtomicInteger(0);
                    CompletableFuture.runAsync(() -> {
                        for (int bbsIndex = 0; bbsIndex < bbs.length; bbsIndex++) {
                            IntegerAxisAlignBB newBB = bbs[bbsIndex];
                            player.sendMessage("Starting task " + bbsIndex + "...");
                            long startMillisForSection = System.currentTimeMillis();
                            // Based on them to check whether it's finished or not
                            AtomicLong queryBlockTimes = new AtomicLong(0);
                            long maxCount = newBB.getSize();
                            AtomicLong lastTipPercentage = new AtomicLong(0);
                            AtomicLong readBlockCountForSection = new AtomicLong(0);
                            CompoundTag tag = new CompoundTag().putList(new ListTag<>("blocks"));
                            int finalBbsIndex = bbsIndex;
                            newBB.forEach(((i, i1, i2) -> {
                                Position position = new Position(i, i1, i2, player.getLevel());
                                if (!position.getChunk().isLoaded()) {
                                    try {
                                        position.getChunk().load(true);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                Block block = player.getLevel().getBlock(position, true);
                                if (block.getId() != Block.AIR) {
                                    int x = BigDecimal.valueOf(i).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinX())).intValue();
                                    int y = BigDecimal.valueOf(i1).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinY())).intValue();
                                    int z = BigDecimal.valueOf(i2).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinZ())).intValue();
                                    CompoundTag addTag = new CompoundTag()
                                            .putInt("x", x)
                                            .putInt("y", y)
                                            .putInt("z", z)
                                            .putInt("blockId", block.getId())
                                            .putInt("damage", block.getDamage());
                                    Block blockLayer1 = player.getLevel().getBlock(position, 1, true);
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
                                        player.sendMessage(builder.toString());
                                        GameAPI.getInstance().getLogger().info(builder.toString());
                                    } else {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append("[").append(finalBbsIndex).append("] ")
                                                .append(" Finish reading the section. The section contains nothing but air blocks. Turning into next sections...");
                                        player.sendMessage(builder.toString());
                                        GameAPI.getInstance().getLogger().info(builder.toString());
                                    }
                                }
                            }));
                        }
                    }, THREAD_POOL_EXECUTOR).thenRun(() -> {
                                StringBuilder builder = new StringBuilder();
                                builder.append("Finish generating building task! ")
                                        .append("Section count: ").append(sectionCount.get())
                                        .append(" | ")
                                        .append("Block count: ").append(readBlockCountAll.get())
                                        .append(" | ")
                                        .append("Time cost: ").append(SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForAll));
                                commandSender.sendMessage(builder.toString());
                                GameAPI.getInstance().getLogger().info(builder.toString());
                                generate = false;
                            }
                    );
                    break;
                case "loadschematics":
                    SchematicConverter.createBuildFromSchematic(player, strings[1]);
                    break;
                case "createbuild":
                    if (generate) {
                        GameAPI.getInstance().getLogger().info("You have started a task of creating or saving build. Please wait...");
                    }
                    File[] files = new File(GameAPI.getPath() + "/buildings/" + strings[1] + "/").listFiles();
                    if (files == null) {
                        commandSender.sendMessage("Cannot find folder");
                        return false;
                    }
                    Position startPos = new Position(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.getLevel());
                    generate = true;
                    startMillisForAll = System.currentTimeMillis();
                    int maxGenerateSections = files.length;
                    AtomicInteger blockCount = new AtomicInteger();
                    AtomicInteger generateSectionCount = new AtomicInteger();
                    commandSender.sendMessage("Start generating building task... [Count: " + maxGenerateSections + "]");
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
                                    int x = startPos.getFloorX() + blocks.getInt("x");
                                    int y = startPos.getFloorY() + blocks.getInt("y");
                                    int z = startPos.getFloorZ() + blocks.getInt("z");
                                    if (y > 256 || y < 0) {
                                        System.out.println("Out of world's bound!");
                                        continue;
                                    }
                                    Block block = Block.get(blocks.getInt("blockId"), blocks.getInt("damage"));
                                    if (block == null) {
                                        block = new BlockUnknown(blocks.getInt("blockId"), blocks.getInt("damage"));
                                    }
                                    if (block.getId() != BlockID.AIR) {
                                        Vector3 pos = new Vector3(x, y, z);
                                        player.getLevel().setBlock(pos, block, true, false);
                                        if (generated.get() >= (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                            GameAPI.getInstance().getLogger().info("[" + blockCount + "] Generating block... §e" + lastTipPercentage.get() + "%");
                                            lastTipPercentage.getAndAdd(5);
                                        }
                                        CompoundTag layer1 = blocks.getCompound("layer1");
                                        int blockLayer1Id = layer1.getInt("blockId");
                                        int blockLayer1Damage = layer1.getInt("damage");
                                        if (blockLayer1Id != 0) {
                                            player.getLevel().setBlock(pos, 1, Block.get(blockLayer1Id, blockLayer1Damage), true, false);
                                        }
                                    }
                                    // GameAPI.plugin.getLogger().info("Generating block info at {" + x + ", " + y + ", " + z + "} with {" + block.getId() + ":" + block.getDamage() + "}");
                                    generated.getAndIncrement();
                                    blockCount.getAndIncrement();
                                }
                                generateSectionCount.getAndIncrement();
                                String timeDiffToString = SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForSection);
                                commandSender.sendMessage("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                                GameAPI.getInstance().getLogger().info("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                            }
                        }).exceptionally(throwable -> {
                            GameAPI.getInstance().getLogger().error(throwable.toString());
                            return null;
                        }).thenRun(() -> {
                            generate = false;
                            commandSender.sendMessage("Finish all saving tasks! Section Count: " + generateSectionCount + ". Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForAll));
                        });
                    } catch (CompletionException e) {
                        GameAPI.getInstance().getLogger().error(e.toString());
                    }
                    break;
            }
        }
        return false;
    }

    public boolean isPosSet(Player player) {
        PosSet posSet = posSetLinkedHashMap.get(player);
        if (posSet == null) {
            player.sendMessage("Pos set is null");
            return false;
        }
        if (posSet.getPos1() == null || posSet.getPos2() == null) {
            player.sendMessage("You haven't set pos1 or pos2");
            return false;
        }
        return true;
    }
}
