package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;

import java.io.File;
import java.util.*;

/**
 * @author Glorydark
 * Based on lt-name's sourcecode in CrystalWar
 */
public class WorldTools {

    public static Map<String, Integer> gameBackUpLoadId = new LinkedHashMap<>();

    @Internal
    public static void delWorldByPrefix(String prefix) {
        String rootPath = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator;
        for (File file : Objects.requireNonNull(new File(rootPath).listFiles())) {
            if (file.getName().startsWith(prefix)) {
                Level level = Server.getInstance().getLevelByName(file.getName());
                if (level == null) {
                    FileTools.delete(file);
                    GameAPI.getInstance().getLogger().warning("删除已复制地图，地图名:" + file.getName());
                    continue;
                }
                if (!unloadLevel(level, true)) {
                    GameAPI.getInstance().getLogger().warning("发现地图无法卸载，地图名:" + file.getName());
                } else {
                    GameAPI.getInstance().getLogger().warning("删除已复制地图，地图名:" + file.getName());
                }
            }
        }
    }

    protected static boolean deleteWorld(String saveWorld) {
        String worldPath = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator + saveWorld + File.separator;
        File file = new File(worldPath);
        return FileTools.delete(file);
    }

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
                room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
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
                p.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
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
            if (Server.getInstance().unloadLevel(level, true)) {
                return deleteWorld(levelName);
            } else {
                return false;
            }
        } else {
            return Server.getInstance().unloadLevel(level, true);
        }
    }

    protected static boolean reloadLevel(Room room, String worldLoadName) {

        // 开始根据备份重载
        File targetDir = new File(Server.getInstance().getDataPath() + "/worlds/" + worldLoadName);
        File backupDir = new File(GameAPI.getPath() + "/worlds/" + room.getRoomLevelBackup());

        if (!backupDir.exists()) {
            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.not_found", worldLoadName));
        }

        if (FileTools.delete(targetDir)) {
            if (FileTools.copyFiles(backupDir, targetDir)) {
                if (Server.getInstance().loadLevel(worldLoadName)) {
                    Level loadLevel = Server.getInstance().getLevelByName(worldLoadName);
                    if (loadLevel != null) {
                        initLevel(loadLevel);
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
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, "internal");
                        GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("world.load.success", worldLoadName));
                        return true;
                    }
                } else {
                    GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", worldLoadName));
                    room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
                }
            } else {
                GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", worldLoadName));
                room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
            }
        } else {
            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", worldLoadName));
            room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
        }
        return false;
    }

    public static boolean loadLevelFromBackup(String loadName, String backupName) {
        return loadLevelFromBackup(loadName, new File(GameAPI.getPath() + File.separator + "worlds" + File.separator + backupName + File.separator));
    }

    public static boolean loadLevelFromBackup(String loadName, File path) {
        Level level = Server.getInstance().getLevelByName(loadName);
        if (level != null) {
            unloadLevel(level, true);
        }
        String worldPath = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator + loadName + File.separator;

        if (path.exists()) {
            if (FileTools.copyFiles(path.getPath(), worldPath)) {
                if (Server.getInstance().loadLevel(loadName)) {
                    initLevel(Server.getInstance().getLevelByName(loadName));
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean saveFromLevel(String levelName, File savePath) {
        Level level = Server.getInstance().getLevelByName(levelName);
        if (level != null) {
            unloadLevel(level, true);
        }
        savePath.mkdirs();
        if (FileTools.copyFiles(GameAPI.getInstance().getServer().getDataPath() + "/worlds/" + levelName + "/", savePath.getPath())) {
            return true;
        }
        return false;
    }

    public static void initLevel(Level level) {
        level.getGameRules().setGameRule(GameRule.SHOW_TAGS, true);
        level.getGameRules().setGameRule(GameRule.LOCATOR_BAR, false);
        level.getGameRules().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        level.getGameRules().setGameRule(GameRule.SPAWN_RADIUS, 0);
        level.setThundering(false);
        level.setRaining(false);
        level.setAutoSave(false);
        level.setSaveOnUnloadEnabled(false);
    }

    public static int getNewBackUpLoadId(String gameName) {
        int result = gameBackUpLoadId.getOrDefault(gameName, 0) + 1;
        gameBackUpLoadId.put(gameName, result);
        return result;
    }
}
