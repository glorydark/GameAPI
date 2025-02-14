package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Glorydark
 * Based on lt-name's sourcecode in CrystalWar
 */
public class WorldTools {

    public static boolean delWorldByPrefix(String prefix) {
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
                    continue;
                } else {
                    GameAPI.getInstance().getLogger().warning("删除已复制地图，地图名:" + file.getName());
                }
            }
        }
        return true;
    }

    protected static boolean deleteWorld(String saveWorld) {
        String worldPath = Server.getInstance().getDataPath()  + File.separator + "worlds" + File.separator + saveWorld + File.separator;
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

    protected static boolean reloadLevel(Room room, String loadName) {

        // 开始根据备份重载
        File levelFile = new File(Server.getInstance().getDataPath() + "/worlds/" + loadName);

        File backup = new File(GameAPI.getPath() + "/worlds/" + room.getRoomLevelBackup());

        if (!backup.exists()) {
            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.not_found", loadName));
        }

        if (FileTools.delete(levelFile)) {
            if (FileTools.copy(backup, levelFile)) {
                if (Server.getInstance().loadLevel(loadName)) {
                    Level loadLevel = Server.getInstance().getLevelByName(loadName);
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
                        GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("world.load.success", loadName));
                        return true;
                    }
                } else {
                    GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
                    room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
                }
            } else {
                GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
                room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
            }
        } else {
            GameAPI.getInstance().getLogger().error(GameAPI.getLanguage().getTranslation("world.load.failed", loadName));
            room.setRoomStatus(RoomStatus.ROOM_MAP_LOAD_FAILED, "internal");
        }
        return false;
    }

    public static boolean loadLevelFromBackup(String loadName, String backupName) {
        Level level = Server.getInstance().getLevelByName(loadName);
        if (level != null) {
            unloadLevel(level, true);
        }

        String savePath = GameAPI.getPath() + File.separator + "worlds" + File.separator + backupName + File.separator;
        String worldPath = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator + loadName + File.separator;

        AtomicBoolean finishCopy = new AtomicBoolean(false);

        // 创建 CompletableFuture 链
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (new File(savePath).exists()) {
                if (new File(worldPath).exists()) {
                    FileTools.delete(new File(worldPath));
                }
                if (FileTools.copy(savePath, worldPath)) {
                    finishCopy.set(true);
                }
            }
        }).thenAcceptAsync(unused -> {
            if (Server.getInstance().loadLevel(loadName)) {
                initLevel(Server.getInstance().getLevelByName(loadName));
            }
        }).thenRunAsync(() -> {
            finishCopy.set(true);
            GameAPI.getGameDebugManager().info("地图加载完成: " + loadName);
        });

        // 等待所有任务完成
        future.join(); // 或者 future.get()

        return finishCopy.get();
    }

    public static void initLevel(Level level) {
        level.setThundering(false);
        level.setRaining(false);
        level.setAutoSave(false);
        level.setSaveOnUnloadEnabled(false);
        level.getGameRules().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }
}
