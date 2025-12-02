package gameapi.room.status.factory;

import gameapi.room.status.*;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.base.DefaultCustomRoomStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
public class RoomDefaultStatusFactory {

    public static final Map<String, CustomRoomStatus> REGISTRY = new HashMap<>();

    public static final String ROOM_STATUS_WAIT_ID = "wait";
    public static final String ROOM_STATUS_PRESTART_ID = "prestart";
    public static final String ROOM_STATUS_READY_START_ID = "ready_start";
    public static final String ROOM_STATUS_GAME_START_ID = "game_start";
    public static final String ROOM_STATUS_GAME_END_ID = "game_end";
    public static final String ROOM_STATUS_CEREMONY_ID = "ceremony";
    public static final String ROOM_STATUS_NEXT_ROUND_PRESTART_ID = "next_round_prestart";

    public static final String ROOM_STATUS_ROOM_END_ID = "room_end";

    public static final String ROOM_MAP_INITIALIZING_ID = "map_initializing";
    public static final String ROOM_MAP_LOAD_FAILED_ID = "map_load_failed";

    public static final String ROOM_HALTED_ID = "room_halted";
    public static final String ROOM_STOPPED_ID = "room_stopped";

    public static final String ROOM_PLAYBACK_ID = "room_playback";
    public static final String ROOM_EDIT_ID = "room_edit";

    public static final String ROOM_INITIALIZING_ID = "room_initializing";

    // 预定义的状态常量
    public static final CustomRoomStatus ROOM_STATUS_WAIT = register(new RoomStatusWait());
    public static final CustomRoomStatus ROOM_STATUS_PRESTART = register(new RoomStatusPrestart());
    public static final CustomRoomStatus ROOM_STATUS_READY_START = register(new RoomStatusReadyStart());
    public static final CustomRoomStatus ROOM_STATUS_GAME_START = register(new RoomStatusGameStart());
    public static final CustomRoomStatus ROOM_STATUS_GAME_END = register(new RoomStatusGameEnd());
    public static final CustomRoomStatus ROOM_STATUS_CEREMONY = register(new RoomStatusCeremony());
    public static final CustomRoomStatus ROOM_STATUS_NEXT_ROUND_PRESTART = register(new RoomStatusNextRoundPrestart());
    public static final CustomRoomStatus ROOM_STATUS_ROOM_END = register(new RoomStatusEnd());

    public static final CustomRoomStatus ROOM_MAP_INITIALIZING = register(new DefaultCustomRoomStatus(ROOM_MAP_INITIALIZING_ID));
    public static final CustomRoomStatus ROOM_MAP_LOAD_FAILED = register(new DefaultCustomRoomStatus(ROOM_MAP_LOAD_FAILED_ID));

    public static final CustomRoomStatus ROOM_HALTED = register(new DefaultCustomRoomStatus(ROOM_HALTED_ID));
    public static final CustomRoomStatus ROOM_STOPPED = register(new DefaultCustomRoomStatus(ROOM_STOPPED_ID));

    public static final CustomRoomStatus ROOM_PLAYBACK = register(new RoomStatusPlayback());
    public static final CustomRoomStatus ROOM_EDIT = register(new DefaultCustomRoomStatus(ROOM_EDIT_ID));

    public static final CustomRoomStatus ROOM_INITIALIZING = register(new DefaultCustomRoomStatus(ROOM_INITIALIZING_ID));

    public static final List<CustomRoomStatus> DEFAULT_ROOM_STATUS_LIST = new ArrayList<>() {
        {
            this.add(ROOM_STATUS_WAIT);
            this.add(ROOM_STATUS_PRESTART);
            this.add(ROOM_STATUS_READY_START);
            this.add(ROOM_STATUS_GAME_START);
            this.add(ROOM_STATUS_GAME_END);
            this.add(ROOM_STATUS_CEREMONY);
            this.add(ROOM_STATUS_ROOM_END);
        }
    };

    /**
     * get a custom room status by identifier
     * @param identifier status identifier
     * @return new CustomRoomStatus instance
     */
    public static @Nullable CustomRoomStatus getByIdentifier(String identifier) {
        return REGISTRY.getOrDefault(identifier, null);
    }

    /**
     * Register a custom room status
     * @param status your custom room status
     */
    public static CustomRoomStatus register(CustomRoomStatus status) {
        REGISTRY.put(status.getIdentifier(), status);
        return status;
    }

    /**
     * Check if status is registed
     * @param identifier status identifier
     * @return isRegistered
     */
    public static boolean isRegistered(String identifier) {
        return REGISTRY.containsKey(identifier);
    }
}