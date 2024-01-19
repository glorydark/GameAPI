package gameapi.commands;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.entity.GameEntityCreator;
import gameapi.ranking.RankingSortSequence;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.SoundTools;
import gameapi.toolkit.InventoryTools;
import gameapi.toolkit.LevelTools;
import gameapi.toolkit.SmartTools;
import gameapi.utils.IntegerAxisAlignBB;
import gameapi.utils.PosSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Glorydark
 * For in-game test
 */
public class BaseCommand extends Command {

    public static LinkedHashMap<Player, PosSet> posSetLinkedHashMap = new LinkedHashMap<>();

    public static ForkJoinPool THREAD_POOL_EXECUTOR;

    public boolean generate;

    public BaseCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isOp() && commandSender.isPlayer()) {
            if (strings.length == 1) {
                switch (strings[0]) {
                    case "quit":
                        Room room = Room.getRoom((Player) commandSender);
                        if (room != null) {
                            if (room.getPlayers().contains((Player) commandSender)) {
                                room.removePlayer((Player) commandSender);
                            } else {
                                room.removeSpectator((Player) commandSender);
                            }
                        } else {
                            GameAPI.getLanguage().getTranslation("command.error.notInGame");
                        }
                        break;
                }
            }
            return true;
        }
        if (strings.length > 0) {
            switch (strings[0].toLowerCase()) {
                case "playressound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            SoundTools.playResourcePackOggMusic(player, strings[2]);
                        } else {
                            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerNotFound", strings[1]));
                        }
                    }
                    break;
                case "playambientsound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            Sound sound = Sound.valueOf(strings[2]);
                            if (sound.getSound() != null) {
                                SoundTools.addAmbientSound(player.level, player, sound);
                            } else {
                                GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.vanillaSoundNotFound", strings[1]));
                            }
                        } else {
                            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerNotFound", strings[1]));
                        }
                    }
                    break;
                case "debug":
                    if (strings.length != 2) {
                        return false;
                    }
                    if (commandSender.isPlayer()) {
                        switch (strings[1]) {
                            case "true":
                                GameAPI.debug.add((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.on"));
                                break;
                            case "false":
                                GameAPI.debug.remove((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.off"));
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.useInGame"));
                    }
                    break;
                case "worldedit":
                    if (strings.length != 2) {
                        return false;
                    }
                    if (commandSender.isPlayer()) {
                        switch (strings[1]) {
                            case "true":
                                GameAPI.worldEditPlayers.add((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.on"));
                                break;
                            case "false":
                                GameAPI.worldEditPlayers.remove((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.off"));
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.useInGame"));
                    }
                    break;
                case "savebattles": // For Tournament Restart Procedures
                    File saveDic = new File(GameAPI.path + "/saves/" + SmartTools.dateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss") + "/");
                    if (saveDic.exists() || saveDic.mkdirs()) {
                        for (String key : GameAPI.loadedRooms.keySet()) {
                            for (Room room : GameAPI.loadedRooms.get(key)) {
                                if (room.getRoomStatus().equals(RoomStatus.ROOM_STATUS_GameStart)) {
                                    File file = new File(saveDic.getPath() + "/" + key + "_" + room.getRoomName() + ".json");
                                    Config config = new Config(file, Config.JSON);
                                    LinkedHashMap<String, Object> players = new LinkedHashMap<>();
                                    room.getPlayers().forEach(player -> players.put(player.getName(), getPlayerData(player)));
                                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                                    objectMap.put("players", players);
                                    objectMap.put("players_properties", getPropertiesData(room.getPlayerProperties()));
                                    objectMap.put("room_properties", room.getRoomProperties());
                                    objectMap.put("room_datas", new Gson().fromJson(room.toString(), Map.class));
                                    config.setAll(objectMap);
                                    config.save();
                                }
                            }
                        }
                    } else {
                        GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.saveBattle.folderCreatedFailed", saveDic.getPath()));
                    }
                    break;
                case "addrank":
                    if (commandSender.isPlayer()) {
                        if (strings.length == 3) {
                            Player player = (Player) commandSender;
                            GameEntityCreator.addRankingList(player, strings[1], strings[2], RankingSortSequence.DESCEND);
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.useInGame"));
                    }
                    break;
                case "stoproom":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                                GameAPI.playerRoomHashMap.remove(player);
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STOPPED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "halt":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room.notProcessing"));
                                return true;
                            }
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                            }
                            room.setRoomStatus(RoomStatus.ROOM_HALTED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "restart":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                if (player.isOnline()) {
                                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                    player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                                } else {
                                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerOffline", player.getName()));
                                }
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "status":
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.getting"));
                    if (GameAPI.loadedRooms.size() > 0) {
                        for (Map.Entry<String, List<Room>> game : GameAPI.loadedRooms.entrySet()) {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.title", game));
                            List<Room> rooms = game.getValue();
                            if (rooms.size() > 0) {
                                for (Room room : rooms) {
                                    if (room.getRoomRule().isNeedPreStartPass()) {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.needStartPass", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    } else {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.common", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    }
                                }
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.noRoomLoaded"));
                            }
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.noGameLoaded"));
                    }
                    break;
                case "roomstart":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
                                room.setPreStartPass(true);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.startPass.endowed", room.getRoomName()));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "setpwd":
                    if (strings.length == 4) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
                                room.setJoinPassword(strings[3]);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.setPassword", strings[3]));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "pos1":
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    Player player = (Player) commandSender;
                    if (!posSetLinkedHashMap.containsKey(player)) {
                        posSetLinkedHashMap.put(player, new PosSet());
                    }
                    posSetLinkedHashMap.get(player).setPos1(player.getLocation());
                    player.sendMessage("Successfully set pos1 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "pos2":
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    player = (Player) commandSender;
                    if (!posSetLinkedHashMap.containsKey(player)) {
                        posSetLinkedHashMap.put(player, new PosSet());
                    }
                    posSetLinkedHashMap.get(player).setPos2(player.getLocation());
                    player.sendMessage("Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                    break;
                case "fill": // fill blockId blockMeta
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    player = (Player) commandSender;
                    PosSet posSet = posSetLinkedHashMap.get(player);
                    if (posSet == null) {
                        player.sendMessage("Pos set is null");
                        return false;
                    }
                    if (posSet.getPos1() == null || posSet.getPos2() == null) {
                        player.sendMessage("You haven't set pos1 or pos2");
                        return false;
                    }
                    if (strings.length == 3) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Block block = Block.get(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                bb.forEach(((i, i1, i2) -> player.getLevel().setBlock(i, i1, i2, block, true, true)));
                                player.sendMessage("Finish fill task");
                            }
                        });
                    }
                    break;
                case "replace":
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    player = (Player) commandSender;
                    posSet = posSetLinkedHashMap.get(player);
                    if (posSet == null) {
                        player.sendMessage("Pos set is null");
                        return false;
                    }
                    if (posSet.getPos1() == null || posSet.getPos2() == null) {
                        player.sendMessage("You haven't set pos1 or pos2");
                        return false;
                    }
                    if (strings.length == 3) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Level level = player.getLevel();
                        Block block = SmartTools.getBlockfromString(strings[1]);
                        boolean checkBlockDamage = (strings[1].split(":").length == 2);
                        Block blockReplaced = SmartTools.getBlockfromString(strings[2]);
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    if (level.getBlock(i, i1, i2).getId() == block.getId()) {
                                        if (checkBlockDamage) {
                                            if (level.getBlock(i, i1, i2).getDamage() == block.getDamage()) {
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
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    player = (Player) commandSender;
                    posSet = posSetLinkedHashMap.get(player);
                    if (posSet == null) {
                        player.sendMessage("Pos set is null");
                        return false;
                    }
                    if (posSet.getPos1() == null || posSet.getPos2() == null) {
                        player.sendMessage("You haven't set pos1 or pos2");
                        return false;
                    }
                    if (strings.length == 2) {
                        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                        Level level = player.getLevel();
                        Block blockReplaced = SmartTools.getBlockfromString(strings[1]);
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    if (level.getBlock(i, i1, i2) instanceof BlockUnknown) {
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
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    player = (Player) commandSender;
                    player.getLevel().regenerateChunk(player.getChunkX(), player.getChunkZ());
                    player.sendMessage("Reset chunk successfully!");
                    break;
                case "savebuild":
                    // /gameapi savebuild 631 71 -256
                    if (generate) {
                        GameAPI.plugin.getLogger().info("You have started a task of creating or saving build. Please wait...");
                    }
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    generate = true;

                    player = (Player) commandSender;
                    Vector3 p1 = new Vector3(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    Vector3 p2 = new Vector3(player.getFloorX(), player.getFloorY(), player.getFloorZ());
                    IntegerAxisAlignBB integerAxisAlignBB = new IntegerAxisAlignBB(p1, p2);
                    String name = String.valueOf(System.currentTimeMillis());
                    long startMillisForAll = System.currentTimeMillis();

                    IntegerAxisAlignBB[] bbs = LevelTools.splitSimpleAxisAlignedBB(integerAxisAlignBB, 64, 100, 64);
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
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    File[] files = new File(GameAPI.path + "/buildings/" + strings[1] + "/").listFiles();
                    if (files == null) {
                        commandSender.sendMessage("Cannot find folder");
                        return false;
                    }
                    player = (Player) commandSender;
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
                case "seeuuid":
                    if (strings.length == 2) {
                        String playerName = strings[1];
                        Player seePlayer = Server.getInstance().getPlayer(playerName);
                        if (seePlayer != null) {
                            GameAPI.plugin.getLogger().info("玩家" + playerName + "的uuid是：" + seePlayer.getUniqueId().toString());
                        } else {
                            Optional<UUID> offlineUUID = Server.getInstance().lookupName(playerName);
                            if (offlineUUID.isPresent()) {
                                IPlayer seePlayerOffline = Server.getInstance().getOfflinePlayer(offlineUUID.get());
                                GameAPI.plugin.getLogger().info("玩家" + playerName + "的uuid是：" + seePlayerOffline.getUniqueId().toString());
                            } else {
                                GameAPI.plugin.getLogger().info("玩家" + playerName + "不存在");
                            }
                        }
                    }
                    break;
            }
        }
        return false;
    }

    public LinkedHashMap<String, Object> getPlayerData(Player player) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ() + ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(InventoryTools.getItemString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for (Item item : player.getInventory().getArmorContents()) {
            armors.add(InventoryTools.getItemString(item));
        }
        maps.put("ArmorContents", armors);
        return maps;
    }

    public LinkedHashMap<String, Object> getPropertiesData(LinkedHashMap<String, LinkedHashMap<String, Object>> playerObjectMap) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        for (String key : playerObjectMap.keySet()) {
            maps.put(key, playerObjectMap.get(key));
        }
        return maps;
    }
}
