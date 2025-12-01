package gameapi.room;

/**
 * @author Glorydark
 */
public enum RoomStatus {

    ROOM_STATUS_WAIT("wait"),
    ROOM_STATUS_PRESTART("prestart"),
    ROOM_STATUS_READY_START("ready_start"),
    ROOM_STATUS_START("game_start"),
    ROOM_STATUS_GAME_END("game_end"),
    ROOM_STATUS_CEREMONY("ceremony"),
    ROOM_STATUS_NEXT_ROUND_PRESTART("next_round_prestart"),

    ROOM_STATUS_END("room_end"),
    ROOM_MAP_INITIALIZING("map_initializing"),
    ROOM_MAP_LOAD_FAILED("map_load_failed"),

    ROOM_HALTED("room_halted"),
    ROOM_STOPPED("room_stopped"),

    ROOM_PLAYBACK("room_playback"),
    ROOM_EDIT("room_edit"),
    ROOM_INITIALIZING("room_initializing");

    private final String identifier;

    RoomStatus(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}