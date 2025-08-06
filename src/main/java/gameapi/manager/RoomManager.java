package gameapi.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.room.edit.EditProcess;
import gameapi.room.utils.reason.QuitRoomReason;
import gameapi.tools.RandomTools;
import gameapi.tools.WorldTools;
import gameapi.utils.RoomNameUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author glorydark
 */
public class RoomManager {

    protected static final Map<String, List<Room>> loadedRooms = new LinkedHashMap<>(); //房间状态

    protected static final Map<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间

    public static void loadRoom(Room room, RoomStatus baseStatus) {
        RoomNameUtils.initializeRoomNameAndId(room);
        List<Room> rooms = new ArrayList<>(loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.add(room);
        loadedRooms.put(room.getGameName(), rooms);
        room.setRoomStatus(baseStatus, "internal");
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
            GameAPI.getGameDebugManager().info("关闭线程池成功: " + room.getRoomTaskExecutor().toString());
            room.setRoomTaskExecutor(null);
        }

        for (Player player : room.getPlayers()) {
            room.removePlayer(player, QuitRoomReason.ROOM_UNLOAD);
        }

        for (Player player : new ArrayList<>(room.getSpectators())) {
            room.removeSpectator(player);
        }

        if (!room.getPlayers().isEmpty()) {
            for (Player player : room.getPlayers()) {
                player.kick("Teleport Error...");
            }
            for (Player player : room.getSpectators()) {
                player.kick("Teleport Error...");
            }
        }

        List<Room> rooms = new ArrayList<>(loadedRooms.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.remove(room);
        loadedRooms.put(room.getGameName(), rooms);
    }

    public static List<Room> getRooms(Level level) {
        List<Room> rooms = new ArrayList<>();
        for (List<Room> roomList : RoomManager.loadedRooms.values()) {
            for (Room room : roomList) {
                if (room.getPlayLevels().contains(level)) {
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    public static Room getRoom(String gameName, Player player) {
        if (getRoom(player) == null) {
            return null;
        }
        return getRoom(player).getGameName().equals(gameName) ? getRoom(player) : null;
    }

    public static Room getRoom(Player player) {
        return RoomManager.playerRoomHashMap.getOrDefault(player, null);
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

    public static Room getRoom(int roomNumber) {
        if (roomNumber == -1) {
            return null;
        }
        Room room;
        for (Map.Entry<String, List<Room>> entry : loadedRooms.entrySet()) {
            for (Room r : entry.getValue()) {
                if (r.getRoomNumber() == roomNumber) {
                    room = r;
                    return room;
                }
            }
        }
        return null;
    }

    public static int getAvailableRoomNumber() {
        List<Integer> integers = new ArrayList<>();
        for (Map.Entry<String, List<Room>> entry : loadedRooms.entrySet()) {
            for (Room r : entry.getValue()) {
                if (r.getRoomNumber() != -1) {
                    integers.add(r.getRoomNumber());
                }
            }
        }
        int newRoomNumber = RandomTools.getRandom(100000, 999999);
        if (integers.stream().noneMatch(integer -> integer == newRoomNumber)) {
            return newRoomNumber;
        }
        return getAvailableRoomNumber();
    }

    public static List<Room> getCreatedRoom(Player player) {
        String playerName = player.getName();
        List<Room> rooms = new ArrayList<>();
        for (List<Room> roomList : loadedRooms.values()) {
            for (Room room : roomList) {
                if (room.getCreator().equals(playerName)) {
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    public static List<Room> getCreatedRoom(String gameName, Player player) {
        String playerName = player.getName();
        List<Room> rooms = new ArrayList<>();
        for (Room room : loadedRooms.getOrDefault(gameName, new ArrayList<>())) {
            if (room.getCreator().equals(playerName)) {
                rooms.add(room);
            }
        }
        return rooms;
    }

    public static List<String> getGameNameList() {
        return new ArrayList<>(loadedRooms.keySet());
    }

    public static void close() {
        Set<String> prefixes = new HashSet<>();
        for (String s : GameListenerRegistry.getListeners().keySet()) {
            if (s.equals(GameListenerRegistry.KEY_GLOBAL_LISTENER)) {
                continue;
            }
            prefixes.add(s + "_");
        }
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            Room room = RoomManager.getRoom(player);
            if (room != null) {
                String prefix = room.getGameName() + "_";
                if (!room.getTempWorldPrefixOverride().isEmpty()) {
                    prefix = room.getTempWorldPrefixOverride();
                }
                prefixes.add(prefix);
                if (room.getPlayers().contains(player)) {
                    room.removePlayer(player, QuitRoomReason.ROOM_UNLOAD);
                } else {
                    room.removeSpectator(player);
                }
            } else {
                for (EditProcess editProcess : GameAPI.editProcessList) {
                    Player editor = editProcess.getPlayer();
                    if (editor == player) {
                        editProcess.onQuit();
                        editProcess.clearAllTextEntities();
                    }
                }
            }
        }
        for (Map.Entry<String, List<Room>> entry : RoomManager.getLoadedRooms().entrySet()) {
            for (Room room : entry.getValue()) {
                room.getRoomTaskExecutor().shutdownNow();
            }
        }
        for (String gameName : prefixes) {
            WorldTools.delWorldByPrefix(gameName);
        }
        loadedRooms.clear();
        playerRoomHashMap.clear();
    }

    public static int getRoomCount() {
        return loadedRooms.size();
    }

    @Internal
    public static Map<Player, Room> getPlayerRoomHashMap() {
        return playerRoomHashMap;
    }

    @Internal
    public static Map<String, List<Room>> getLoadedRooms() {
        return loadedRooms;
    }
}
