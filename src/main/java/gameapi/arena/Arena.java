package gameapi.arena;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
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

    public static Boolean reloadLevel(Room room){
        Level level = Server.getInstance().getLevelByName(room.getStartLevel());
        if(level == null){
            MainClass.plugin.getLogger().error("§c游戏房间: "+ room.getRoomName() +"地图还原失败！请检查文件权限！");
            return false;
        }
        String levelName = level.getName();
        if(level.getPlayers().values().size() > 0){
            for(Player p:level.getPlayers().values()){
                p.teleportImmediate(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
            }
        }
        if(level.getPlayers().values().size() > 0){
            for(Player p:level.getPlayers().values()){
                p.kick("Due to a unprecedented error,please rejoin the server");
            }
        }
        for(Entity e: level.getEntities()){
            e.kill();
            e.close();
        }
        Server.getInstance().unloadLevel(level, true);
        File levelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + levelName);
        File backup = new File(MainClass.path + "/worlds/" + room.getRoomLevelBackup());
        if (!backup.exists()) {
            MainClass.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图备份不存在！还原失败！");
        }
        CompletableFuture.runAsync(() -> {
            if (FileUtil.deleteFile(levelFile) && FileUtil.copyDir(backup, levelFile)) {
                if(Server.getInstance().loadLevel(levelName) && Server.getInstance().isLevelLoaded(levelName)) {
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                    MainClass.plugin.getLogger().info("§a游戏房间: " + levelName + " 地图还原完成！");
                }else{
                    MainClass.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图还原失败！请检查文件权限！");
                    MainClass.RoomHashMap.remove(room);
                    return;
                }
            } else {
                MainClass.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图还原失败！请检查文件权限！");
                MainClass.RoomHashMap.remove(room);
                return;
            }
        }, MainClass.THREAD_POOL_EXECUTOR);
        return true;
    }
}
