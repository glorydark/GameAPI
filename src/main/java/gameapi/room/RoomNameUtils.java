package gameapi.room;


import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author glorydark
 * @date {2024/1/6} {11:45}
 */
public class RoomNameUtils {

    public static ConcurrentHashMap<String, Integer> tempRoomRecord = new ConcurrentHashMap<>();

    public static void initializeRoomName(Room room) {
        if (room.getRoomName().isEmpty()) {
            if (room.isTemporary()) {
                if (!tempRoomRecord.containsKey(room.getGameName())) {
                    tempRoomRecord.put(room.getGameName(), 0);
                }
                int num = tempRoomRecord.get(room.getGameName()) + 1;
                String name = room.getGameName() + "_temp_" + num;
                tempRoomRecord.put(room.getGameName(), num);
                if (Room.getRoom(room.getGameName(), name) != null) {
                    initializeRoomName(room);
                    return;
                }
                room.setRoomName(name);
            } else {
                String name = room.getGameName() + "_" + UUID.randomUUID();
                if (Room.getRoom(room.getGameName(), name) == null) {
                    room.setRoomName(name);
                } else {
                    initializeRoomName(room);
                }
            }
        }
    }
}
