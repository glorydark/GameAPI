package gameapi.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.manager.tools.GameEntityManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.tools.WorldTools;
import gameapi.utils.RoomNameUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author glorydark
 */
public class RoomManager {

    protected static final LinkedHashMap<String, List<Room>> loadedRooms = new LinkedHashMap<>(); //房间状态

    protected static final LinkedHashMap<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间

    public static void loadRoom(Room room, RoomStatus baseStatus) {
        RoomNameUtils.initializeRoomNameAndId(room);
        List<Room> rooms = new ArrayList<>(loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.add(room);
        loadedRooms.put(room.getGameName(), rooms);
        room.setRoomStatus(baseStatus);
        room.getRoomTaskExecutor().scheduleAtFixedRate(room.getRoomUpdateTask(), 0, GameAPI.GAME_TASK_INTERVAL * 50, TimeUnit.MILLISECONDS);
    }

    public static void unloadRoom(String gameName) {
        for (Room room : getRooms(gameName)) {
            unloadRoom(room);
        }
    }

    public static void unloadRoom(Room room) {
        if (room.getRoomTaskExecutor() != null) {
            room.getRoomTaskExecutor().shutdownNow();
            GameDebugManager.info("关闭线程池成功: " + room.getRoomTaskExecutor().toString());
        }
        for (Player player : room.getPlayers()) {
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
        }

        if (room.getPlayers().size() > 0) {
            for (Player player : room.getPlayers()) {
                player.kick("Teleport Error...");
            }
        }
        List<Room> rooms = new ArrayList<>(loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.remove(room);
        loadedRooms.put(room.getGameName(), rooms);
    }

    public static Optional<Room> getRoom(Level level) {
        if (RoomManager.playerRoomHashMap.size() > 0) {
            return RoomManager.playerRoomHashMap.values().stream().filter(room -> room != null && room.getPlayLevels().stream().anyMatch(l -> l == level)).findFirst();
        } else {
            return Optional.empty();
        }
    }

    public static Room getRoom(String gameName, Player p) {
        if (getRoom(p) == null) {
            return null;
        }
        return getRoom(p).getGameName().equals(gameName) ? getRoom(p) : null;
    }

    public static Room getRoom(Player p) {
        return RoomManager.playerRoomHashMap.getOrDefault(p, null);
    }

    public static Room getRoom(String gameName, String roomName) {
        for (Room room : RoomManager.getRooms(gameName)) {
            if (room.getGameName().equals(gameName) && room.getRoomName().equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    public static List<Room> getRooms(String gameName) {
        return new ArrayList<>(loadedRooms.getOrDefault(gameName, new ArrayList<>()));
    }

    public static List<String> getGameNameList() {
        return new ArrayList<>(loadedRooms.keySet());
    }

    public static void close() {
        for (Map.Entry<String, List<Room>> entry : loadedRooms.entrySet()) {
            for (Room room : entry.getValue()) {
                String prefix = room.getGameName() + "_";
                if (!room.getTempWorldPrefixOverride().isEmpty()) {
                    prefix = room.getTempWorldPrefixOverride();
                }
                WorldTools.delWorldByPrefix(prefix);
            }
        }
        GameEntityManager.closeAll();
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
    }

    public static int getRoomCount() {
        return loadedRooms.size();
    }

    @Internal
    public static LinkedHashMap<Player, Room> getPlayerRoomHashMap() {
        return playerRoomHashMap;
    }

    @Internal
    public static LinkedHashMap<String, List<Room>> getLoadedRooms() {
        return loadedRooms;
    }
}
