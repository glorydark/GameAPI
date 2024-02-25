package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;
import gameapi.commands.data.WorldEditOperation;
import gameapi.commands.data.entry.OperationEntry;
import gameapi.commands.data.entry.SimpleOperationEntry;
import gameapi.tools.SmartTools;
import gameapi.utils.IntegerAxisAlignBB;
import gameapi.utils.PosSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public static LinkedHashMap<Player, WorldEditOperation> lastOperation = new LinkedHashMap<>();

    public boolean generate;

    public WorldEditCommand(String name) {
        super(name);
        this.commandParameters.clear();
        this.commandParameters.put("sudo", new CommandParameter[]{
                CommandParameter.newType("sudo", CommandParamType.TEXT)
        });
        this.commandParameters.put("resetc", new CommandParameter[]{
                CommandParameter.newType("resetc", CommandParamType.TEXT)
        });
        this.commandParameters.put("pos1", new CommandParameter[]{
                CommandParameter.newType("pos1", CommandParamType.TEXT)
        });
        this.commandParameters.put("pos2", new CommandParameter[]{
                CommandParameter.newType("pos2", CommandParamType.TEXT)
        });
        this.commandParameters.put("fill", new CommandParameter[]{
                CommandParameter.newType("fill", CommandParamType.TEXT)
        });
        this.commandParameters.put("createbuild", new CommandParameter[]{
                CommandParameter.newType("startX", CommandParamType.TEXT),
                CommandParameter.newType("startY", CommandParamType.TEXT),
                CommandParameter.newType("startZ", CommandParamType.TEXT)
        });
        this.commandParameters.put("savebuild", new CommandParameter[]{
                CommandParameter.newType("startX", CommandParamType.TEXT),
                CommandParameter.newType("startY", CommandParamType.TEXT),
                CommandParameter.newType("startZ", CommandParamType.TEXT)
        });
        this.commandParameters.put("worldedit", new CommandParameter[]{
                CommandParameter.newType("worldedit", CommandParamType.TEXT),
                CommandParameter.newType("state", CommandParamType.TEXT)
        });
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
                case "sudo":
                    if (lastOperation.containsKey(player)) {
                        if (lastOperation.get(player).sudo()) {
                            commandSender.sendMessage(TextFormat.GREEN + "Successfully sudo the last worldedit operation!");
                        }
                    } else {
                        commandSender.sendMessage(TextFormat.RED + "Cannot sudo operations because you haven't execute any operation yet.");
                    }
                case "worldedit":
                    if (strings.length != 2) {
                        return false;
                    }
                    switch (strings[1]) {
                        case "true":
                            GameAPI.worldEditPlayers.add((Player) commandSender);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.on"));
                            break;
                        case "false":
                            GameAPI.worldEditPlayers.remove((Player) commandSender);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.off"));
                    }
                    break;
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
                    if (strings.length == 3) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Block block = Block.get(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                List<OperationEntry> simpleOperationEntries = new ArrayList<>();
                                bb.forEach(((i, i1, i2) -> {
                                    Block before = player.getLevel().getBlock(i, i1, i2);
                                    simpleOperationEntries.add(SimpleOperationEntry.builder()
                                                .beforeBlockId(before.getId())
                                                .beforeBlockMeta(before.getDamage())
                                                .floorX(i)
                                                .floorY(i1)
                                                .floorZ(i2)
                                            .build());
                                    player.getLevel().setBlock(i, i1, i2, block, true, true);
                                }));
                                player.sendMessage("Finish fill task");
                                WorldEditOperation worldEditOperation = WorldEditOperation.builder()
                                        .changedBlockEntries(simpleOperationEntries)
                                        .build();
                                lastOperation.put(player, worldEditOperation);
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
                        Block block = SmartTools.getBlockfromString(strings[1]);
                        boolean checkBlockDamage = (strings[1].split(":").length == 2);
                        Block blockReplaced = SmartTools.getBlockfromString(strings[2]);
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                List<OperationEntry> simpleOperationEntries = new ArrayList<>();
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    Block before = player.getLevel().getBlock(i, i1, i2);
                                    if (before.getId() == block.getId()) {
                                        if (checkBlockDamage) {
                                            if (before.getDamage() == block.getDamage()) {
                                                return;
                                            }
                                        }
                                        simpleOperationEntries.add(SimpleOperationEntry.builder()
                                                .beforeBlockId(before.getId())
                                                .beforeBlockMeta(before.getDamage())
                                                .floorX(i)
                                                .floorY(i1)
                                                .floorZ(i2)
                                                .build());
                                        level.setBlock(i, i1, i2, blockReplaced, true, true);
                                        count.getAndIncrement();
                                    }
                                }));
                                WorldEditOperation worldEditOperation = WorldEditOperation.builder()
                                        .changedBlockEntries(simpleOperationEntries)
                                        .build();
                                lastOperation.put(player, worldEditOperation);
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
                        Block blockReplaced = SmartTools.getBlockfromString(strings[1]);
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                List<OperationEntry> simpleOperationEntries = new ArrayList<>();
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    Block before = player.getLevel().getBlock(i, i1, i2);
                                    if (before instanceof BlockUnknown) {
                                        simpleOperationEntries.add(SimpleOperationEntry.builder()
                                                .beforeBlockId(before.getId())
                                                .beforeBlockMeta(before.getDamage())
                                                .floorX(i)
                                                .floorY(i1)
                                                .floorZ(i2)
                                                .build());
                                        level.setBlock(i, i1, i2, blockReplaced, true, true);
                                        count.getAndIncrement();
                                    }
                                }));
                                WorldEditOperation worldEditOperation = WorldEditOperation.builder()
                                        .changedBlockEntries(simpleOperationEntries)
                                        .build();
                                lastOperation.put(player, worldEditOperation);
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
                        GameAPI.plugin.getLogger().info("You have started a task of creating or saving build. Please wait...");
                    }
                    generate = true;
                    Vector3 p1 = new Vector3(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    Vector3 p2 = new Vector3(player.getFloorX(), player.getFloorY(), player.getFloorZ());
                    IntegerAxisAlignBB integerAxisAlignBB = new IntegerAxisAlignBB(p1, p2);
                    String name = String.valueOf(System.currentTimeMillis());
                    long startMillisForAll = System.currentTimeMillis();

                    IntegerAxisAlignBB[] bbs = integerAxisAlignBB.splitAABB(64, 100, 64);
                    GameAPI.plugin.getLogger().info("Start building save task in {" + integerAxisAlignBB + "}");
                    player.sendMessage("Start building save task in {" + integerAxisAlignBB + "}");

                    player.sendMessage("For better performance, the range are cut into various sections: ");
                    GameAPI.plugin.getLogger().info("For better performance, the range are cut into various sections: ");
                    for (int i = 0; i < bbs.length; i++) {
                        player.sendMessage("- [" + i + "] " + bbs[i].toString());
                        GameAPI.plugin.getLogger().info("- [" + i + "] " + bbs[i].toString());
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
                                Block block = player.getLevel().getBlock(i, i1, i2, true);
                                if (block.getId() != Block.AIR) {
                                    int x = BigDecimal.valueOf(i).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinX())).intValue();
                                    int y = BigDecimal.valueOf(i1).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinY())).intValue();
                                    int z = BigDecimal.valueOf(i2).subtract(BigDecimal.valueOf(integerAxisAlignBB.getMinZ())).intValue();
                                    tag.getList("blocks", CompoundTag.class).add(new CompoundTag()
                                            .putInt("x", x)
                                            .putInt("y", y)
                                            .putInt("z", z)
                                            .putInt("blockId", block.getId())
                                            .putInt("damage", block.getDamage())
                                    );
                                    readBlockCountAll.getAndIncrement();
                                    readBlockCountForSection.getAndIncrement();
                                }
                                queryBlockTimes.getAndIncrement();
                                if (queryBlockTimes.get() > (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                    GameAPI.plugin.getLogger().info("[" + finalBbsIndex + "] Saving sections... §e" + lastTipPercentage + "%");
                                    lastTipPercentage.getAndAdd(5);
                                }
                                if (queryBlockTimes.get() >= newBB.getSize()) {
                                    if (readBlockCountForSection.get() != 0) {
                                        new File(GameAPI.path + "/buildings/" + name + "/").mkdirs();
                                        File file = new File(GameAPI.path + "/buildings/" + name + "/" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".nbt");
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
                                        GameAPI.plugin.getLogger().info(builder.toString());
                                    } else {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append("[").append(finalBbsIndex).append("] ")
                                                .append(" Finish reading the section. The section contains nothing but air blocks. Turning into next sections...");
                                        player.sendMessage(builder.toString());
                                        GameAPI.plugin.getLogger().info(builder.toString());
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
                                GameAPI.plugin.getLogger().info(builder.toString());
                                generate = false;
                            }
                    );
                    break;
                case "createbuild":
                    if (generate) {
                        GameAPI.plugin.getLogger().info("You have started a task of creating or saving build. Please wait...");
                    }
                    File[] files = new File(GameAPI.path + "/buildings/" + strings[1] + "/").listFiles();
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
                    GameAPI.plugin.getLogger().info("Start generating building task... [Count: " + maxGenerateSections + "]");
                    try {
                        CompletableFuture.runAsync(() -> {
                            long startMillisForSection = System.currentTimeMillis();
                            for (File file : files) {
                                CompoundTag compoundTag;
                                try {
                                    compoundTag = NBTIO.read(file);
                                } catch (IOException e) {
                                    GameAPI.plugin.getLogger().error(e.toString());
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
                                    player.getLevel().setBlock(new Vector3(x, y, z), block, true, false);

                                    if (generated.get() >= (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                        GameAPI.plugin.getLogger().info("[" + blockCount + "] Generating block... §e" + lastTipPercentage.get() + "%");
                                        lastTipPercentage.getAndAdd(5);
                                    }
                                    // GameAPI.plugin.getLogger().info("Generating block info at {" + x + ", " + y + ", " + z + "} with {" + block.getId() + ":" + block.getDamage() + "}");
                                    generated.getAndIncrement();
                                    blockCount.getAndIncrement();
                                }
                                generateSectionCount.getAndIncrement();
                                String timeDiffToString = SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForSection);
                                commandSender.sendMessage("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                                GameAPI.plugin.getLogger().info("Finish saving building task [" + generateSectionCount + "/ " + maxGenerateSections + "]! Time cost: " + timeDiffToString);
                            }
                        }).exceptionally(throwable -> {
                            GameAPI.plugin.getLogger().error(throwable.toString());
                            return null;
                        }).thenRun(() -> {
                            generate = false;
                            commandSender.sendMessage("Finish all saving tasks! Section Count: " + generateSectionCount + ". Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillisForAll));
                        });
                    } catch (CompletionException e) {
                        GameAPI.plugin.getLogger().error(e.toString());
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
