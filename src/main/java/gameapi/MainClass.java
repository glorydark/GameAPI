package gameapi;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import gameapi.arena.Arena;
import gameapi.event.PlayerEvent;
import gameapi.room.Room;
import gameapi.task.RoomTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainClass extends PluginBase implements Listener {

    public static List<Room> RoomHashMap = Collections.synchronizedList(new ArrayList<>()); //房间状态
    public static String path = null;
    public static Plugin plugin = null;
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 4),
            new ThreadPoolExecutor.DiscardPolicy());
    public static HashMap<String, Map<String, Object>> gameRecord = new HashMap<>();
    public static HashMap<String, Integer> format = new HashMap<>();

    static {
        //format.put("properties", 0);
        //format.put("con", 0);
        //format.put("conf", 0);
        //format.put("config", 0);
        //format.put("js", 1);
        format.put("json", 1);
        format.put("yml", 2);
        format.put("yaml", 2);
        //format.put("txt", 5);
        //format.put("list", 5);
        //format.put("enum", 5);
    }

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();
        File file = new File(path+"/worlds/");
        File file1 = new File(path+"/gameRecords/");
        file.mkdirs();
        file1.mkdir();
        loadAllGameRecord();
        this.getLogger().info("DGameAPI Enabled!");
        this.getServer().getScheduler().scheduleRepeatingTask(plugin,new RoomTask(),20);
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(),this);
    }

    @Override
    public void onLoad() {
        this.getLogger().info("DGameAPI OnLoad!");
        this.getLogger().info("作者:glorydark");
        path = this.getDataFolder().getPath();
        plugin = this;
    }

    public void loadAllGameRecord(){
        File[] files = new File(path+"/gameRecords/").listFiles();
        if(files != null && files.length > 0){
            for(File file:files){
                String fileName = file.getName().split("\\.")[0];
                String formatString = file.getName().split("\\.")[1];
                Config config;
                Integer checkFormat;
                if(format.containsKey(formatString)) {
                    checkFormat = format.get(formatString);
                }else{
                    this.getLogger().error("Unable to deal with the gameRecord File["+file.getName()+"]because of unsupported format!");
                    return;
                }
                config = new Config(file.getPath(),checkFormat);
                if(config != null) {
                    gameRecord.put(fileName, config.getAll());
                }else{
                    this.getLogger().error("Unable to deal with the gameRecord File["+file.getName()+"]because of unsupported format!");
                    return;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DGameAPI Disabled!");
    }
}
