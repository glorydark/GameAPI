package gameapi.utils;


import gameapi.manager.RoomManager;
import gameapi.room.Room;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author glorydark
 * @date {2024/1/6} {11:45}
 */
public class RoomNameUtils {

    public static ConcurrentHashMap<String, Integer> tempRoomRecord = new ConcurrentHashMap<>();

    public static void initializeRoomNameAndId(Room room) {
        int num = tempRoomRecord.getOrDefault(room.getGameName(), 0) + 1;
        room.setId(num);
        if (room.getRoomName().isEmpty()) {
            if (room.isTemporary()) {
                String name = getNumericNumberName(room.getGameName());
                if (RoomManager.getRoom(room.getGameName(), name) != null) {
                    initializeRoomNameAndId(room);
                    return;
                }
                room.setRoomName(name);
            } else {
                String name = room.getGameName() + "_" + UUID.randomUUID();
                if (RoomManager.getRoom(room.getGameName(), name) == null) {
                    room.setRoomName(name);
                } else {
                    initializeRoomNameAndId(room);
                }
            }
        }
    }

    public static String getNumericNumberName(String gameName) {
        int num = tempRoomRecord.getOrDefault(gameName, 0) + 1;
        if (!tempRoomRecord.containsKey(gameName)) {
            tempRoomRecord.put(gameName, 0);
        }
        String name = gameName + "_temp_" + num;
        tempRoomRecord.put(gameName, num);
        return name;
    }
}
