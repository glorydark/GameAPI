package gameapi.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.manager.tools.GameEntityManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.tools.WorldTools;
import gameapi.utils.RoomNameUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author glorydark
 */
public class RoomManager {

    public static LinkedHashMap<String, List<Room>> loadedRooms = new LinkedHashMap<>(); //房间状态

    public static LinkedHashMap<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间

    public static void loadRoom(Room room, RoomStatus baseStatus) {
        RoomNameUtils.initializeRoomName(room);
        List<Room> rooms = new ArrayList<>(loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.add(room);
        loadedRooms.put(room.getGameName(), rooms);
        room.setRoomStatus(baseStatus);
        Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.plugin, room.getRoomUpdateTask(), GameAPI.GAME_TASK_INTERVAL);
    }

    public static void unloadRoom(Room room) {
        room.getRoomUpdateTask().cancel();
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
            return RoomManager.playerRoomHashMap.values().stream().filter(room -> room != null && room.getPlayLevels().stream().anyMatch(l -> l.equals(level))).findFirst();
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
        for (Room room : RoomManager.loadedRooms.getOrDefault(gameName, new ArrayList<>())) {
            if (room.getGameName().equals(gameName) && room.getRoomName().equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    public static void close() {
        loadedRooms.keySet().forEach(WorldTools::delWorldByPrefix);
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
}
