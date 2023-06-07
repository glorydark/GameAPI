package gameapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.arena.Arena;
import gameapi.commands.AdminCommands;
import gameapi.entity.EntityTools;
import gameapi.languages.Language;
import gameapi.listener.BaseEventListener;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.SimpleRanking;
import gameapi.room.Room;
import gameapi.task.RoomTask;
import gameapi.utils.GameRecord;
import gameapi.utils.GsonAdapter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Glorydark
 * Some methods using in this class came from others, and you can find the original author in some specific classes!
 */
public class GameAPI extends PluginBase implements Listener {

    public static ConcurrentHashMap<String, List<Room>> RoomHashMap = new ConcurrentHashMap<>(); //房间状态
    public static HashMap<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间
    public static String path = null;

    public static Plugin plugin = null;
    public static HashMap<String, Map<String, Object>> gameRecord = new HashMap<>();
    public static List<Player> debug = new ArrayList<>();
    public static int entityRefreshIntervals = 100;
    public static boolean saveBag;

    public static boolean allow_move_event;

    //此处引用lt-name的CrystalWar内的复原地图部分源码
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            5,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            task -> new Thread(task, "GameAPI Restore World Thread")
    );

    protected static List<String> loadedGames = new ArrayList<>();

    protected static Language language = new Language("GameAPI");

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        plugin = this;
        this.getDataFolder().mkdir();
        this.saveDefaultConfig();
        this.saveResource("rankings.yml", false);
        this.saveResource("languages/zh_CN.properties", false);
        this.saveResource("languages/en_US.properties", false);
        language.addLanguage(new File(path+"/languages/zh_CN.properties"));
        language.addLanguage(new File(path+"/languages/en_US.properties"));
        File file = new File(path+"/worlds/");
        File file1 = new File(path+"/gameRecords/");
        file.mkdirs();
        file1.mkdir();
        Config config = new Config(path+"/config.yml", Config.YAML);
        saveBag = config.getBoolean("save_bag", false);
        allow_move_event = config.getBoolean("allow_move_event", true);
        language.setDefaultLanguage(config.getString("default_language", "zh_CN"));
        //loadSkills();
        loadAllGameRecord();
        if(new File(path+"/rankings.yml").exists()){
            Config rankingConfig = new Config(path+"/rankings.yml", Config.YAML);
            /*
                Allow users to customize ranking format.
             */
            if(rankingConfig.exists("format")){
                RankingFormat rankingFormat = new RankingFormat(rankingConfig.getString("score_show_format"), rankingConfig.getString("champion_prefix"), rankingConfig.getString("runnerUp_prefix"), rankingConfig.getString("secondRunnerUp_prefix"));
                GameRecord.setRankingFormat(rankingFormat);
            }
        }
        loadAllRankingListEntities();
        this.getServer().getScheduler().scheduleRepeatingTask(plugin, new RoomTask(),20, true);
        this.getServer().getPluginManager().registerEvents(new BaseEventListener(),this);
        //GameListenerRegistry.registerEvents("test", new TestListener(), this);
        //GameListenerRegistry.callEvent("test", new RoomPlayerJoinEvent(null, null));
        //AdvancedBlockRegistry.registerAdvancedBlock(152, 0, TestAdvancedBlockListenerListener.class);
        this.getServer().getCommandMap().register("",new AdminCommands("gameapi"));
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
                        out +=  "yaw: " + df.format(player.getYaw()) + " pitch: " + df.format(player.pitch) + " headYaw: " + df.format(player.headYaw) + "\n";
                        Item item = player.getInventory().getItemInHand();
                        out += "手持物品id: [" + item.getId() + ":" + item.getDamage() + "] 数量:" + item.getCount() + "\n";
                        Block block = player.getTargetBlock(32);
                        if (block != null) {
                            out += "所指方块id: [" + block.getId() + ":" + block.getDamage() + "] 方块名称:" + block.getName() + "\n";
                            out += "所指方块位置: [" + df.format(block.getX()) + ":" + df.format(block.getY()) + ":" + df.format(block.getZ()) + "]" + "\n";
                        } else {
                            out += "所指方块id: [无] 方块名称:无"  + "\n";
                            out += "所指方块位置: [无]"  + "\n";
                        }
                        Block under = player.getLocation().add(0, 0, 0).getLevelBlock();
                        if(under != null){
                            out+= "所踩方块: "+under.getId()+":"+under.getDamage();
                        }else{
                            out+= "所踩方块: [无]";
                        }
                        player.sendActionBar(out);
                }
                );
            }
        }, 5);
        this.getLogger().info("§aDGameAPI Enabled!");
    }
    @Override
    public void onLoad() {
        this.getLogger().info("§aDGameAPI OnLoad!");
        this.getLogger().info("§aAuthor:glorydark");
    }

    public void loadAllGameRecord(){
        File[] files = new File(path+"/gameRecords/").listFiles();
        if(files != null && files.length > 0){
            for(File file:files){
                String fileName = file.getName().split("\\.")[0];
                Config config;
                config = new Config(file.getPath(), Config.YAML);
                gameRecord.put(fileName, config.getAll());
            }
        }
    }

    public void loadAllRankingListEntities(){
        Config config = new Config(path+ "/rankings.yml");
        entityRefreshIntervals = config.getInt("refresh_interval", 100);
        List<Map<String, Object>> maps = config.get("list", new ArrayList<>());
        for(Map<String, Object> map: maps){
            String level = (String) map.get("level");
            if(Server.getInstance().getLevelByName(level) == null){
                if(!Server.getInstance().loadLevel(level)){
                    this.getLogger().warning(language.getTranslation("loading.ranking_loader.unknown_world", level));
                    continue;
                }else{
                    this.getLogger().info(language.getTranslation("loading.ranking_loader.world_onLoad", level));
                }
            }else{
                this.getLogger().info(language.getTranslation("loading.ranking_loader.world_alreadyLoaded", level));
            }
            Location location = new Location((Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), this.getServer().getLevelByName((String) map.get("level")));
            if(location.getChunk() == null){
                if(!location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ())){
                    this.getLogger().info(language.getTranslation("loading.ranking_loader.chunk_onLoad", location.getChunkX(), location.getChunkZ()));
                    return;
                }else{
                    this.getLogger().warning(language.getTranslation("loading.ranking_loader.chunk_loadedFailed", location.getChunkX(), location.getChunkZ()));
                }
            }else{
                this.getLogger().info(language.getTranslation("loading.ranking_loader.chunk_alreadyLoaded", location.getChunkX(), location.getChunkZ()));
            }
            Ranking ranking = new SimpleRanking(location, (String) map.get("game_name"), "No Data", new RankingFormat(), RankingSortSequence.DESCEND, (String) map.get("game_name"), (String) map.get("compared_type"));
            ranking.spawnEntity();
        }
    }

    @Override
    public void onDisable() {
        loadedGames.forEach(s -> Arena.delWorld(s));
        EntityTools.closeAll();
        THREAD_POOL_EXECUTOR.shutdown();
        RoomHashMap.clear();
        playerRoomHashMap.clear();
        gameRecord.clear();
        GameListenerRegistry.clearAllRegisters();
        this.getLogger().info("DGameAPI Disabled!");
    }

    public static boolean saveJsonToCore(String savePath, InputStream stream, int type){
        File file = new File(path + "/" + savePath);
        if(file.exists()){ return true; }
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(), new GsonAdapter()).create();
        Map<String, Object> mainMap;
        JsonReader reader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8)); //一定要以utf-8读取
        mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
        Config config = new Config(file, type);
        LinkedHashMap<String, Object> save = new LinkedHashMap<>();
        mainMap.keySet().forEach(key -> save.put(key, mainMap.get(key)));
        config.setAll(save);
        config.save();
        return true;
    }

    public static void addLoadedGame(String gameName){
        loadedGames.add(gameName);
    }

    public static void removeLoadedGame(String gameName){
        loadedGames.remove(gameName);
    }

    public static List<String> getLoadedGames() {
        return loadedGames;
    }

    public static Language getLanguage() {
        return language;
    }

}
