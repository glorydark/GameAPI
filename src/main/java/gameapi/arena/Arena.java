package gameapi.arena;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import gameapi.MainClass;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.FileHandler;
import gameapi.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Arena {
    public static Boolean saveWorld(Level world){
        String rootPath = Server.getInstance().getDataPath()+"/worlds/"+world.getName()+"/";
        String savePath = MainClass.path+"/worlds/"+world.getName()+"/";
        try {
            FileHandler.copyDir(rootPath,savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean copyWorld(String loadName,String backupName){
        Server.getInstance().broadcastMessage(Server.getInstance().getDataPath());
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+loadName+"/";
        String savePath = MainClass.path+"/worlds/"+backupName+"/";
        try {
            FileHandler.copyDir(savePath, worldPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean copyWorldAndLoad(String loadName,String backupName){
        String savePath = MainClass.path+"/worlds/"+backupName+"/";
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+loadName+"/";
        try {
            FileHandler.copyDir(savePath, worldPath);
            Server.getInstance().loadLevel(loadName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean deleteDir(String saveWorld){
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+saveWorld+"/";
        File file = new File(worldPath);
        if(file.isDirectory()) {
            FileHandler.delete(file);
        }
        return true;
    }

    public static void createVoidWorld(String worldname){
        Server.getInstance().generateLevel(worldname);
    }

    /* 钻石大陆
    public static void createDIYWorld(String worldname){
        Generator.addGenerator(FlatDIY.class, "DIY", Generator.TYPE_FLAT);
        Server.getInstance().generateLevel(worldname,0, Generator.getGenerator("DIY"));
        Server.getInstance().loadLevel(worldname);
    }

     */

    public static void reloadLevel(Room room){
        Level level = Server.getInstance().getLevelByName(room.getStartLevel());
        if(level == null){
            MainClass.plugin.getLogger().error("§c游戏房间: "+ room.getRoomName() +"地图还原失败！请检查文件权限！");
            return;
        }
        String levelName = level.getName();
        if(level.getPlayers().values().size() > 0){
            for(Player p:level.getPlayers().values()){
                p.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
            }
        }
        if(level.getPlayers().values().size() > 0){
            for(Player p:level.getPlayers().values()){
                p.kick("Due to a unprecedented error,please rejoin the server");
            }
        }
        Server.getInstance().unloadLevel(level, true);
        File levelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + levelName);
        File backup = new File(MainClass.path + "/worlds/" + room.getRoomLevelBackup());
        if (!backup.exists()) {
            MainClass.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图备份不存在！还原失败！");
            return;
        }
        CompletableFuture.runAsync(() -> {
            if (FileUtil.deleteFile(levelFile) && FileUtil.copyDir(backup, levelFile)) {
                Server.getInstance().loadLevel(levelName);
                room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                MainClass.plugin.getLogger().info("§a游戏房间: " + levelName + " 地图还原完成！");
            }else {
                MainClass.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图还原失败！请检查文件权限！");
                MainClass.RoomHashMap.remove(room);
                return;
            }
        }, MainClass.EXECUTOR);
    }
}
