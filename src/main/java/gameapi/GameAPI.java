package gameapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Config;
import gameapi.arena.WorldTools;
import gameapi.commands.BaseCommand;
import gameapi.entity.GameEntityCreator;
import gameapi.form.AdvancedFormMain;
import gameapi.gameLevel.GameLevelSystem;
import gameapi.language.Language;
import gameapi.listener.BaseEventListener;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.SimpleRanking;
import gameapi.room.Room;
import gameapi.room.RoomEdit;
import gameapi.room.RoomNameUtils;
import gameapi.room.RoomStatus;
import gameapi.task.RoomTask;
import gameapi.utils.GameRecord;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Glorydark
 * Some methods using in this class came from others, and you can find the original author in some specific classes!
 */
public class GameAPI extends PluginBase implements Listener {

    public static ConcurrentHashMap<String, List<Room>> loadedRooms = new ConcurrentHashMap<>(); //房间状态
    public static LinkedHashMap<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间
    public static String path;
    public static Plugin plugin;
    public static HashMap<String, Map<String, Object>> gameRecord = new HashMap<>();
    public static List<Player> debug = new ArrayList<>();
    public static List<Player> worldEditPlayers = new ArrayList<>();
    public static int entityRefreshIntervals = 100;
    public static boolean saveBag;
    public static boolean tipsEnabled;
    public static HashMap<Player, RoomEdit> editDataHashMap = new HashMap<>();
    public static SimpleAxisAlignedBB autoLoadChunkRange;
    public static GameLevelSystem system;
    protected static Language language = new Language("GameAPI");

    public static void loadRoom(Room room, RoomStatus baseStatus) {
        RoomNameUtils.initializeRoomName(room);
        List<Room> rooms = new ArrayList<>(GameAPI.loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.add(room);
        GameAPI.loadedRooms.put(room.getGameName(), rooms);
        room.setRoomStatus(baseStatus);
        Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.plugin, room.getRoomUpdateTask(), 3);
    }

    public static void unloadRoom(Room room) {
        room.getRoomUpdateTask().cancel();
        for (Player player : room.getPlayers()) {
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
        }

        if (room.getPlayers().size() > 0) {
            for (Player player : room.getPlayers()) {
                player.kick("Teleport Error...");
            }
        }
        List<Room> rooms = new ArrayList<>(GameAPI.loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.remove(room);
        GameAPI.loadedRooms.put(room.getGameName(), rooms);
    }

    public static void addRoomEdit(Player player, RoomEdit roomEdit) {
        roomEdit.init();
        editDataHashMap.put(player, roomEdit);
    }

    public static Language getLanguage() {
        return language;
    }

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        plugin = this;
        this.getDataFolder().mkdir();
        this.saveDefaultConfig();
        this.saveResource("rankings.yml", false);
        this.saveResource("languages/zh_CN.properties", false);
        this.saveResource("languages/en_US.properties", false);
        language.addLanguage(new File(path + "/languages/zh_CN.properties"));
        language.addLanguage(new File(path + "/languages/en_US.properties"));
        File file = new File(path + "/worlds/");
        File file1 = new File(path + "/gameRecords/");
        file.mkdirs();
        file1.mkdir();
        Config config = new Config(path + "/config.yml", Config.YAML);
        autoLoadChunkRange = new SimpleAxisAlignedBB(config.getInt("auto_load_chunk.minX", -2), config.getInt("auto_load_chunk.maxX", 2), 0, 0, config.getInt("auto_load_chunk.minZ", -2), config.getInt("auto_load_chunk.maxZ", 2));
        saveBag = config.getBoolean("save_bag", false);
        language.setDefaultLanguage(config.getString("default_language", "zh_CN"));
        loadAllGameRecord();
        if (new File(path + "/rankings.yml").exists()) {
            Config rankingConfig = new Config(path + "/rankings.yml", Config.YAML);
            if (rankingConfig.exists("format")) {
                RankingFormat rankingFormat = new RankingFormat(rankingConfig.getString("score_show_format"), rankingConfig.getString("champion_prefix"), rankingConfig.getString("runnerUp_prefix"), rankingConfig.getString("secondRunnerUp_prefix"));
                GameRecord.setRankingFormat(rankingFormat);
            }
        }
        loadAllRankingListEntities();
        this.getServer().getScheduler().scheduleRepeatingTask(plugin, new RoomTask(), 20);
        this.getServer().getPluginManager().registerEvents(new BaseEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new AdvancedFormMain(), this);
        this.getServer().getCommandMap().register("", new BaseCommand("gameapi"));
        Server.getInstance().getScheduler().scheduleRepeatingTask(this, new NukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(debug);
                players.forEach(player -> {
                            if (player == null || !player.isOnline()) {
                                debug.remove(player);
                                return;
                            }
                            DecimalFormat df = new DecimalFormat("#0.00");
                            String out = "GameAPI Debug\n";
                            out += "所在位置: [" + df.format(player.getX()) + ":" + df.format(player.getY()) + ":" + df.format(player.getZ()) + "] 世界名: " + player.getLevel().getName() + "\n";
                            out += "yaw: " + df.format(player.getYaw()) + " pitch: " + df.format(player.pitch) + " headYaw: " + df.format(player.headYaw) + "\n";
                            Item item = player.getInventory().getItemInHand();
                            out += "手持物品id: [" + item.getId() + ":" + item.getDamage() + "] 数量:" + item.getCount() + "\n";
                            Block block = player.getTargetBlock(32);
                            if (block != null) {
                                //out += "所指方块id: [" + block.toItem().getNamespaceId() + "] 方块名称:" + block.getName() + "\n";
                                out += "所指方块id: [" + block.getId() + ":" + block.getDamage() + "] 方块名称:" + block.getName() + "\n";
                                out += "所指方块位置: [" + df.format(block.getX()) + ":" + df.format(block.getY()) + ":" + df.format(block.getZ()) + "]" + "\n";
                            } else {
                                out += "所指方块id: [无] 方块名称:无" + "\n";
                                out += "所指方块位置: [无]" + "\n";
                            }
                            Block under = player.getLocation().add(0, 0, 0).getLevelBlock();
                            if (under != null) {
                                //out += "所踩方块: " + under.toItem().getNamespaceId();
                                out += "所踩方块: " + under.getId() + ":" + under.getDamage();
                            } else {
                                out += "所踩方块: [无]";
                            }
                            player.sendActionBar(out);
                        }
                );
            }
        }, 5, true);
        tipsEnabled = this.getServer().getPluginManager().getPlugin("Tips") != null;
        BaseCommand.THREAD_POOL_EXECUTOR = (ForkJoinPool) Executors.newWorkStealingPool();
        this.getLogger().info("§aDGameAPI Enabled!");
    }

    @Override
    public void onLoad() {
        this.getLogger().info("§aDGameAPI OnLoad!");
        this.getLogger().info("§aAuthor:glorydark");
    }

    public void loadAllGameRecord() {
        File[] files = new File(path + "/gameRecords/").listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null && subFiles.length > 0) {
                        for (File subFile : subFiles) {
                            gameRecord.put(file.getName() + "/" + subFile.getName().split("\\.")[0], new Config(subFile.getPath(), Config.YAML).getAll());
                        }
                    }
                }
            }
        }
    }

    public void loadAllRankingListEntities() {
        Config config = new Config(path + "/rankings.yml");
        entityRefreshIntervals = config.getInt("refresh_interval", 100);
        List<Map<String, Object>> maps = config.get("list", new ArrayList<>());
        for (Map<String, Object> map : maps) {
            String level = (String) map.get("level");
            if (Server.getInstance().getLevelByName(level) == null) {
                if (!Server.getInstance().loadLevel(level)) {
                    this.getLogger().warning(language.getTranslation("loading.ranking_loader.unknown_world", level));
                    continue;
                } else {
                    this.getLogger().info(language.getTranslation("loading.ranking_loader.world_onLoad", level));
                }
            } else {
                this.getLogger().info(language.getTranslation("loading.ranking_loader.world_alreadyLoaded", level));
            }
            Location location = new Location((Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), this.getServer().getLevelByName((String) map.get("level")));
            if (location.getChunk() == null) {
                if (!location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ())) {
                    this.getLogger().info(language.getTranslation("loading.ranking_loader.chunk_onLoad", location.getChunkX(), location.getChunkZ()));
                    return;
                } else {
                    this.getLogger().warning(language.getTranslation("loading.ranking_loader.chunk_loadedFailed", location.getChunkX(), location.getChunkZ()));
                }
            } else {
                this.getLogger().info(language.getTranslation("loading.ranking_loader.chunk_alreadyLoaded", location.getChunkX(), location.getChunkZ()));
            }
            Ranking ranking = new SimpleRanking(location, (String) map.getOrDefault("value_type", ""), (String) map.getOrDefault("title", "Undefined"), "No Data", new RankingFormat(), (Boolean) map.getOrDefault("sort_consequence_ascend", false) ? RankingSortSequence.ASCEND : RankingSortSequence.DESCEND, (String) map.get("game_name"), (String) map.get("compared_type"));
            ranking.spawnEntity();
        }
    }

    @Override
    public void onDisable() {
        loadedRooms.keySet().forEach(WorldTools::delWorldByPrefix);
        GameEntityCreator.closeAll();
        for (String s : loadedRooms.keySet()) {
            for (Room room : loadedRooms.getOrDefault(s, new ArrayList<>())) {
                for (Player player : new ArrayList<>(room.getPlayers())) {
                    room.removePlayer(player);
                }
                for (Player player : new ArrayList<>(room.getSpectators())) {
                    room.removePlayer(player);
                }
            }
        }
        loadedRooms.clear();
        playerRoomHashMap.clear();
        gameRecord.clear();
        GameListenerRegistry.clearAllRegisters();
        BaseCommand.THREAD_POOL_EXECUTOR.shutdown();
        this.getLogger().info("DGameAPI Disabled!");
    }

}
