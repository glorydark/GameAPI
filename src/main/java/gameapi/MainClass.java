package gameapi;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import gameapi.arena.Arena;
import gameapi.event.PlayerEvent;
import gameapi.room.Room;
import gameapi.task.RoomTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainClass extends PluginBase implements Listener {

    public static List<Room> RoomHashMap = new ArrayList<Room>(); //房间状态
    public static String path = null;
    public static Plugin plugin = null;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();
        File file = new File(this.getDataFolder()+"/worlds/");
        file.mkdirs();
        this.getLogger().info("DGameAPI Enabled!");
        this.getServer().getScheduler().scheduleRepeatingTask(new RoomTask(),20);
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(),this);
    }

    @Override
    public void onLoad() {
        this.getLogger().info("DGameAPI OnLoad!");
        path = this.getDataFolder().getPath();
        plugin = this;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DGameAPI Disabled!");
    }
}
