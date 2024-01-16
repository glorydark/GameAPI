package gameapi.commands;

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
import gameapi.entity.EntityTools;
import gameapi.ranking.RankingSortSequence;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.SoundTools;
import gameapi.toolkit.InventoryTools;
import gameapi.toolkit.LevelTools;
import gameapi.toolkit.SmartTools;
import gameapi.utils.PosSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
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
                            EntityTools.addRankingList(player, strings[1], strings[2], RankingSortSequence.DESCEND);
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
                        Block blockReplaced = SmartTools.getBlockfromString(strings[2]);
                        Server.getInstance().getScheduler().scheduleAsyncTask(GameAPI.plugin, new AsyncTask() {
                            @Override
                            public void onRun() {
                                AtomicInteger count = new AtomicInteger();
                                bb.forEach(((i, i1, i2) -> {
                                    if (level.getBlock(i, i1, i2).getId() == block.getId() && level.getBlock(i, i1, i2).getDamage() == block.getDamage()) {
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
                        GameAPI.plugin.getLogger().info("目前正在生成建筑，请稍后...");
                    }
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    generate = true;
                    player = (Player) commandSender;
                    Vector3 p1 = new Vector3(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    Vector3 p2 = new Vector3(player.getFloorX(), player.getFloorY(), player.getFloorZ());
                    SimpleAxisAlignedBB simpleAxisAlignedBB = new SimpleAxisAlignedBB(p1, p2);
                    GameAPI.plugin.getLogger().info("Start building save task in {" + simpleAxisAlignedBB + "}");
                    player.sendMessage("Start building save task in {" + simpleAxisAlignedBB + "}");
                    String name = String.valueOf(System.currentTimeMillis());
                    long startMillis = System.currentTimeMillis();
                    SimpleAxisAlignedBB[] bbs = LevelTools.splitSimpleAxisAlignedBB(simpleAxisAlignedBB, 64, 32, 64);
                    player.sendMessage("For better performance, the range are cut into various sections: ");
                    GameAPI.plugin.getLogger().info("For better performance, the range are cut into various sections: ");
                    for (int i = 0; i < bbs.length; i++) {
                        player.sendMessage("- [" + i + "] " + bbs[i].toString());
                        GameAPI.plugin.getLogger().info("- [" + i + "] " + bbs[i].toString());
                    }
                    CompletableFuture.runAsync(() -> {
                        for (int bbsIndex = 0; bbsIndex < bbs.length; bbsIndex++) {
                            int finalI = bbsIndex;
                            SimpleAxisAlignedBB newBB = bbs[bbsIndex];
                            player.sendMessage("Starting task " + finalI + "...");
                            long startMillis1 = System.currentTimeMillis();
                            AtomicLong count = new AtomicLong();
                            long maxCount = BigDecimal.valueOf(newBB.getMaxX() - newBB.getMinX())
                                    .multiply(BigDecimal.valueOf(newBB.getMaxY() - newBB.getMinY()))
                                    .multiply(BigDecimal.valueOf(newBB.getMaxZ() - newBB.getMinZ()))
                                    .longValue();
                            AtomicLong lastTipPercentage = new AtomicLong(0);
                            AtomicLong readBlockCount = new AtomicLong(0);
                            CompoundTag tag = new CompoundTag().putList(new ListTag<>("blocks"));
                            AtomicInteger sectionCount = new AtomicInteger(0);
                            newBB.forEach(((i, i1, i2) -> {
                                Block block = player.getLevel().getBlock(i, i1, i2);
                                if (block.getId() != Block.AIR) {
                                    int x = BigDecimal.valueOf(i).subtract(BigDecimal.valueOf(simpleAxisAlignedBB.getMinX())).intValue();
                                    int y = BigDecimal.valueOf(i1).subtract(BigDecimal.valueOf(simpleAxisAlignedBB.getMinY())).intValue();
                                    int z = BigDecimal.valueOf(i2).subtract(BigDecimal.valueOf(simpleAxisAlignedBB.getMinZ())).intValue();
                                    tag.getList("blocks", CompoundTag.class).add(new CompoundTag()
                                            .putInt("x", x)
                                            .putInt("y", y)
                                            .putInt("z", z)
                                            .putInt("blockId", block.getId())
                                            .putInt("damage", block.getDamage())
                                    );
                                    // GameAPI.plugin.getLogger().info("Outputing block info from {" + x + ", " + y + ", " + z + "} with {" + block.getName() + ":" + block.getDamage() + "}");
                                    count.getAndIncrement();
                                }
                                readBlockCount.getAndIncrement();
                                if (readBlockCount.get() > (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                                    GameAPI.plugin.getLogger().info("[" + finalI + "] Generating block... §e" + lastTipPercentage + "%");
                                    lastTipPercentage.getAndAdd(5);
                                }
                            }));
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
                            player.sendMessage("[" + finalI + "] Finish building save tasks in " + name + ". Time cost: " + SmartTools.timeDiffMillisToString(startMillis1, System.currentTimeMillis()));
                            GameAPI.plugin.getLogger().info("[" + finalI + "] Finish building save tasks in " + name + ". Time cost: " + SmartTools.timeDiffMillisToString(startMillis1, System.currentTimeMillis()));
                        }}, THREAD_POOL_EXECUTOR).thenRun(() -> {
                        commandSender.sendMessage("Finish generating building task! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis));
                        GameAPI.plugin.getLogger().info("Finish generating building task! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis));
                        generate = false;
                    });
                    break;
                case "createbuild":
                    if (generate) {
                        GameAPI.plugin.getLogger().info("目前正在生成建筑，请稍后...");
                    }
                    if (!commandSender.isPlayer()) {
                        return false;
                    }
                    commandSender.sendMessage("Start generating building task...");
                    GameAPI.plugin.getLogger().info("Start generating building task...");
                    player = (Player) commandSender;
                    Position startPos = new Position(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.getLevel());
                    generate = true;
                    startMillis = System.currentTimeMillis();
                    AtomicInteger i = new AtomicInteger();
                    for (File file : Objects.requireNonNull(new File(GameAPI.path + "/buildings/" + strings[1] + "/").listFiles())) {
                        CompletableFuture.runAsync(() -> {{
                            CompoundTag compoundTag;
                            try {
                                compoundTag = NBTIO.read(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
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
                                    GameAPI.plugin.getLogger().info("[" + i + "] Generating block... §e" + lastTipPercentage.get() + "%");
                                    lastTipPercentage.getAndAdd(5);
                                }
                                // GameAPI.plugin.getLogger().info("Generating block info at {" + x + ", " + y + ", " + z + "} with {" + block.getId() + ":" + block.getDamage() + "}");
                                generated.getAndIncrement();
                                i.getAndIncrement();
                            }
                        }}, THREAD_POOL_EXECUTOR).thenRun(() -> {
                            generate = false;
                            commandSender.sendMessage("Finish all saving building tasks! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis));
                            GameAPI.plugin.getLogger().info("Finish all saving build tasks! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis));
                        });
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
