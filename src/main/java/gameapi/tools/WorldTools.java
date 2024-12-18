package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.NukkitRunnable;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Glorydark
 * Based on lt-name's sourcecode in CrystalWar
 */
public class WorldTools {

    public static boolean delWorldByPrefix(String prefix) {
        String rootPath = Server.getInstance().getDataPath() + "/worlds";
        for (File file : Objects.requireNonNull(new File(rootPath).listFiles())) {
            if (file.getName().startsWith(prefix)) {
                Level level = Server.getInstance().getLevelByName(file.getName());
                if (level == null) {
                    FileTools.delete(file);
                    continue;
                }
                if (!unloadLevel(level, true)) {
                    GameAPI.getInstance().getLogger().warning("发现地图无法卸载，地图名:" + file.getName());
                    continue;
                }
                GameAPI.getInstance().getLogger().warning("删除已复制地图，地图名:" + file.getName());
            }
        }
        return true;
    }

    protected static boolean deleteWorld(String saveWorld) {
        String worldPath = Server.getInstance().getDataPath() + "/worlds/" + saveWorld + "/";
        File file = new File(worldPath);
        return FileTools.delete(file);
    }

    @Deprecated
    public static void createVoidWorld(String worldname) {
        Server.getInstance().generateLevel(worldname);
    }

    /* 钻石大陆
    public static void createDIYWorld(String worldname){
        Generator.addGenerator(FlatDIY.class, "DIY", Generator.TYPE_FLAT);
        Server.getInstance().generateLevel(worldname,0, Generator.getGenerator("DIY"));
        Server.getInstance().loadLevel(worldname);
    }

     */

    public static boolean unloadAndReloadLevels(Room room) {
        List<Level> originList = new ArrayList<>(room.getPlayLevels());
        room.setPlayLevels(new ArrayList<>());
        for (Level playLevel : originList) {
            String loadName = playLevel.getName();
            if (unloadLevel(playLevel, true)) {
                if (!reloadLevel(room, loadName)) {
                    return false;
                }
            } else {
                GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.not_found", room.getRoomName()));
                room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED);
                return false;
            }
        }
        return true;
    }

    public static boolean unloadLevel(Level level, boolean delete) {

        if (level == null || level.getProvider() == null) {
            GameAPI.getGameDebugManager().error(GameAPI.getLanguage().getTranslation("world.load.not_found"));
            return false;
        }

        String levelName = level.getName();

        // 移除玩家
        if (!level.getPlayers().values().isEmpty()) {
            for (Player p : level.getPlayers().values()) {
                p.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
            }
        }

        // 清除实体
        for (Entity e : level.getEntities()) {
            e.kill();
            e.close();
            level.removeEntity(e);
        }

        for (BlockEntity e : level.getBlockEntities().values()) {
            level.removeBlockEntity(e);
        }

        if (delete) {
            //level.tickRateCounter = 99999;
            if (level.unload(true)) {
                return deleteWorld(levelName);
            } else {
                return false;
            }
        } else {
            return level.unload(true);
        }

    }

    protected static boolean reloadLevel(Room room, String loadName) {

        // 开始根据备份重载
        File levelFile = new File(Server.getInstance().getDataPath() + "/worlds/" + loadName);

        File backup = new File(GameAPI.getPath() + "/worlds/" + room.getRoomLevelBackup());

        if (!backup.exists()) {
            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.not_found", loadName));
        }

        final boolean[] b = {true};

        // Try Async Delayed Task
        new NukkitRunnable() {
            @Override
            public void run() {
                if (FileTools.delete(levelFile)) {
                    if (FileTools.copy(backup, levelFile)) {
                        if (Server.getInstance().loadLevel(loadName)) {
                            Level loadLevel = Server.getInstance().getLevelByName(loadName);
                            if (loadLevel != null) {
                                room.addPlayLevel(loadLevel);
                                // Change referred level of each location
                                room.getWaitSpawn().setLevel(loadLevel);
                                for (AdvancedLocation advancedLocation : room.getStartSpawn()) {
                                    advancedLocation.setLevel(loadLevel);
                                }
                                if (room.getEndSpawn() != null && room.getEndSpawn().getLevel().getProvider() == null) {
                                    room.getEndSpawn().setLevel(loadLevel);
                                }
                                for (AdvancedLocation advancedLocation : room.getSpectatorSpawn()) {
                                    advancedLocation.setLevel(loadLevel);
                                }
                                room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("world.load.success", loadName));
                            }
                        } else {
                            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
                            room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED);
                        }
                    } else {
                        GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
                        room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED);
                    }
                } else {
                    GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
                    room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED);
                }
                b[0] = false;
            }
        }.runTaskAsynchronously(GameAPI.getInstance());
        return b[0];
    }

    public static boolean loadLevelFromBackup(String loadName, String backupName) {
        Level level = Server.getInstance().getLevelByName(loadName);
        if (level != null) {
            unloadLevel(level, true);
        }
        String savePath = GameAPI.getPath() + File.separator + "worlds" + File.separator + backupName + File.separator;
        String worldPath = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator + loadName + File.separator;
        if (new File(savePath).exists()) {
            if (FileTools.copy(savePath, worldPath)) {
                return Server.getInstance().loadLevel(loadName);
            }
            return false;
        }
        return false;
    }
}
