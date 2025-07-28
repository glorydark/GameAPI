package gameapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.achievement.AchievementManager;
import gameapi.commands.GameAPICommandMain;
import gameapi.commands.VanillaFixCommand;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.defaults.room.HubCommand;
import gameapi.extensions.particleGun.Weapon;
import gameapi.extensions.particleGun.WeaponManager;
import gameapi.listener.AdvancedFormListener;
import gameapi.listener.BaseEventListener;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.GameDebugManager;
import gameapi.manager.RoomManager;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.GlobalSettingsManager;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.manager.data.RankingManager;
import gameapi.manager.tools.GameEntityManager;
import gameapi.room.edit.EditProcess;
import gameapi.task.RoomTask;
import gameapi.tools.BlockTools;
import gameapi.tools.ItemTools;
import gameapi.utils.Language;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Glorydark
 * Some methods using in this class came from others, and you can find the original author in some specific classes!
 */
public class GameAPI extends PluginBase implements Listener {

    public static final int GAME_TASK_INTERVAL = 1; // this value should not be modified for the roomUpdateTask
    protected static final int THREAD_POOL_SIZE = 8;
    protected static final Language language = new Language("GameAPI");
    public static Set<Player> worldEditPlayers = new HashSet<>();
    public static List<EditProcess> editProcessList = new ArrayList<>();
    public static ForkJoinPool WORLDEDIT_THREAD_POOL_EXECUTOR;
    // Since v_1_21_70, before opening another inventory, the action should wait at least 9 ticks to avoid lapping.
    public static int OPEN_INVENTORY_DELAY_TICKS = 9;
    public static boolean enableParticleWeapon = true;
    protected static GameDebugManager gameDebugManager;
    protected static String path;
    protected static GameAPI instance;
    protected static ScheduledExecutorService roomTaskExecutor;
    protected static ThreadFactory threadFactory = new ThreadFactory() {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setUncaughtExceptionHandler((t, e) -> GameAPI.getInstance().getLogger().error("Thread " + t.getName() + " encountered an error: " + e));
            return thread;
        }
    };
    protected static boolean experimentalFeature = false;
    protected static boolean isFirstLaunch = true;
    protected boolean glorydarkRelatedFeature;
    protected boolean tipsEnabled;
    protected boolean saveTempStates = false;
    protected String timestampApi;

    public static void addRoomEdit(EditProcess editProcess) {
        editProcessList.add(editProcess);
    }

    public static void joinRoomEdit(Player player, EditProcess editProcess) {
        editProcess.begin(player);
    }

    public static GameAPI getInstance() {
        return instance;
    }

    public static String getPath() {
        return path;
    }

    public static Language getLanguage() {
        return language;
    }

    public static GameDebugManager getGameDebugManager() {
        return gameDebugManager;
    }

    public static boolean isExperimentalFeature() {
        return experimentalFeature;
    }

    @Override
    public void onLoad() {
        this.getLogger().info("§aDGameAPI OnLoad!");
        this.getLogger().info("§aAuthor:glorydark");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.YELLOW + "\n" +
                "------------------------------------------------------------------------\n" +
                "  _______      ___      .___  ___.  _______     ___      .______    __  \n" +
                " /  _____|    /   \\     |   \\/   | |   ____|   /   \\     |   _  \\  |  | \n" +
                "|  |  __     /  ^  \\    |  \\  /  | |  |__     /  ^  \\    |  |_)  | |  | \n" +
                "|  | |_ |   /  /_\\  \\   |  |\\/|  | |   __|   /  /_\\  \\   |   ___/  |  | \n" +
                "|  |__| |  /  _____  \\  |  |  |  | |  |____ /  _____  \\  |  |      |  | \n" +
                " \\______| /__/     \\__\\ |__|  |__| |_______/__/     \\__\\ | _|      |__| \n" +
                "                                                                        \n" +
                "                            Author: Glorydark                           \n" +
                "------------------------------------------------------------------------");
        path = this.getDataFolder().getPath();
        instance = this;
        roomTaskExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE, threadFactory);
        gameDebugManager = new GameDebugManager("gameapi_log", new File(path + File.separator + "logs" + File.separator));
        this.getDataFolder().mkdir();
        this.saveDefaultConfig();
        this.saveResource("rankings.yml", false);

        new File(path + File.separator + "logs" + File.separator).mkdirs();
        new File(path + File.separator + "worlds" + File.separator).mkdirs();
        new File(path + File.separator + "gameRecords" + File.separator).mkdirs();
        new File(path + File.separator + "task_caches" + File.separator).mkdirs();
        new File(path + File.separator + "skin_exports" + File.separator).mkdirs();
        new File(path + File.separator + "schematics" + File.separator).mkdirs();
        new File(path + File.separator + "buildings" + File.separator).mkdirs();
        new File(path + File.separator + "activities" + File.separator).mkdirs();
        new File(path + File.separator + "global_settings" + File.separator).mkdirs();
        new File(path + File.separator + "player_caches_old" + File.separator).mkdirs();

        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        gameDebugManager.setEnableConsoleDebug(config.getBoolean("log_show_in_console", true));
        this.glorydarkRelatedFeature = config.getBoolean("glorydark-feature", false);
        this.saveTempStates = config.getBoolean("save-temp-state", true);
        OPEN_INVENTORY_DELAY_TICKS = config.getInt("inventory-menu-open-delay-ticks");
        this.timestampApi = config.getString("timestamp-api", "https://timeapi.io/api/Time/current/zone?timeZone=Asia/Shanghai");
        RankingManager.rankingTextEntityRefreshIntervals = config.getInt("ranking-text-entity-refresh-intervals", 100);
        // load lang data
        this.loadLanguage();
        language.setDefaultLanguage(config.getString("default-language", "zh_CN"));
        this.tipsEnabled = this.getServer().getPluginManager().getPlugin("Tips") != null;

        this.loadAllPlayerGameData();
        this.loadRanking();

        GlobalSettingsManager.init();
        for (Player value : Server.getInstance().getOnlinePlayers().values()) {
            GlobalSettingsManager.loadPlayerData(value);
        }
        GameActivityManager.init();
        AchievementManager.load();

        if (isFirstLaunch) {
            GameListenerRegistry.clearAllRegisters();
            this.getServer().getScheduler().scheduleRepeatingTask(instance, new RoomTask(), 20);
            this.getServer().getPluginManager().registerEvents(new BaseEventListener(), this);
            this.getServer().getPluginManager().registerEvents(new AdvancedFormListener(), this);
            isFirstLaunch = false;
        }
        // this.getServer().getCommandMap().register("", new BaseCommand("gameapi"));
        this.getServer().getCommandMap().register("", new GameAPICommandMain("gameapi"));
        this.getServer().getCommandMap().register("", new WorldEditCommand("worldedit"));
        this.getServer().getCommandMap().register("", new HubCommand("hub"));
        this.getServer().getCommandMap().register("", new VanillaFixCommand("vanilla"));
        // others ...
        roomTaskExecutor.scheduleAtFixedRate(() -> {
            try {
                for (EditProcess editProcess : new ArrayList<>(editProcessList)) {
                    editProcess.onTick();
                    editProcess.getCurrentStep().onTick();
                }
                GameActivityManager.updateTempDataCleaning();
            } catch (Throwable t) {
                GameAPI.getGameDebugManager().printError(t);
            }
        }, 0, 1, TimeUnit.SECONDS);
        roomTaskExecutor.scheduleAtFixedRate(() -> new ArrayList<>(GameAPI.getGameDebugManager().getPlayers()).forEach(player -> {
            if (!player.isOnline() || player.getInventory() == null) {
                return;
            }
            try {
                if (player.isOp()) {
                    DecimalFormat df = new DecimalFormat("#0.00");
                    String out = "GameAPI Debug\n";
                    out += "所在位置: [" + df.format(player.getX()) + ":" + df.format(player.getY()) + ":" + df.format(player.getZ()) + "] 世界名: " + player.getLevel().getName() + "\n";
                    out += "yaw: " + df.format(player.getYaw()) + " pitch: " + df.format(player.pitch) + " headYaw: " + df.format(player.headYaw) + "\n";
                    Item item = player.getInventory().getItemInHand();
                    out += "手持物品id: [" + ItemTools.getIdentifierAndMetaString(item) + " (" + item.getId() + ":" + item.getDamage() + ")] 数量:" + item.getCount() + "\n";
                    Block block = player.getTargetBlock(32);
                    if (block != null) {
                        //out += "所指方块id: [" + block.toItem().getNamespaceId() + "] 方块名称:" + block.getName() + "\n";
                        out += "所指方块id: [" + block.getId() + ":" + block.getDamage() + "] 物品id：" + block.getItemId() + " 方块名称:" + block.getName() + "\n";
                        out += "所指方块位置: [" + df.format(block.getX()) + ":" + df.format(block.getY()) + ":" + df.format(block.getZ()) + "]" + "\n";
                    } else {
                        out += "所指方块id: [无] 方块名称:无" + "\n";
                        out += "所指方块位置: [无]" + "\n";
                    }
                    Block under = player.getLocation().add(0, 0, 0).getLevelBlock();
                    if (under != null) {
                        //out += "所踩方块: " + under.toItem().getNamespaceId();
                        out += "所踩方块: " + BlockTools.getIdentifierWithMeta(under);
                    } else {
                        out += "所踩方块: [无]";
                    }
                    player.sendActionBar(out);
                } else {
                    player.sendActionBar("x: " + player.getX()
                            + "\ny: " + player.getY()
                            + "\nz: " + player.getZ()
                            + "\nyaw: " + player.getYaw()
                            + "\npitch: " + player.getPitch()
                            + "\nheadYaw: " + player.getHeadYaw()
                    );
                }
            } catch (Throwable t) {
                GameAPI.getGameDebugManager().printError(t);
            }
        }), 0, 200, TimeUnit.MILLISECONDS);
        roomTaskExecutor.scheduleAtFixedRate(GameEntityManager::onUpdate, 0, 2, TimeUnit.SECONDS);
        WORLDEDIT_THREAD_POOL_EXECUTOR = (ForkJoinPool) Executors.newWorkStealingPool();

        if (enableParticleWeapon) {
            WeaponManager.init();
            WeaponManager.REGISTERED_WEAPONS.put("test", new Weapon("test", "测试武器", "描述", Item.fromString("minecraft:emerald"), 10, 180, new DustParticle(Position.fromObject(new Vector3()), BlockColor.YELLOW_BLOCK_COLOR), 30, 1, 1.0, 30, 1.0, 1, true, true));
            this.getServer().getPluginManager().registerEvents(new WeaponManager(), this);
        }
        this.getLogger().info("§aDGameAPI Enabled!");
    }

    @Override
    public void onDisable() {
        try {
            AdvancedFormListener.closeAllForms();
            RoomManager.close();
            PlayerGameDataManager.close();
            GameEntityManager.closeAll();
            GameListenerRegistry.clearAllRegisters();
            if (enableParticleWeapon) {
                WeaponManager.EXECUTOR.shutdownNow();
            }
        } catch (Throwable t) {
        }
        roomTaskExecutor.shutdownNow();
        WORLDEDIT_THREAD_POOL_EXECUTOR.shutdownNow();
        this.getLogger().info("GameAPI Disabled!");
    }

    public void loadLanguage() {
        this.saveResource("languages/zh_CN.properties", false);
        this.saveResource("languages/en_US.properties", false);
        language.addLanguage(new File(path + "/languages/zh_CN.properties"));
        language.addLanguage(new File(path + "/languages/en_US.properties"));
    }

    public void loadRanking() {
        RankingManager.despawnAllRankingEntities();
        RankingManager.init();
    }

    public void loadAllPlayerGameData() {
        File[] files = new File(path + "/gameRecords/").listFiles();
        Map<String, Map<String, Object>> playerGameData = new LinkedHashMap<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null) {
                        for (File subFile : subFiles) {
                            if (subFile != null && subFile.isFile()) {
                                String name = file.getName() + "/" + subFile.getName().substring(0, subFile.getName().lastIndexOf("."));
                                playerGameData.put(name, new Config(subFile, Config.YAML).getAll());
                                this.getLogger().info("Loaded player data: " + subFile);
                            }
                        }
                    }
                }
            }
        }
        PlayerGameDataManager.setPlayerGameData(playerGameData);
    }

    public boolean isTipsEnabled() {
        return this.tipsEnabled;
    }

    public boolean isSaveTempStates() {
        return saveTempStates;
    }

    public boolean isGlorydarkRelatedFeature() {
        return glorydarkRelatedFeature;
    }

    public String getTimestampApi() {
        return timestampApi;
    }
}
