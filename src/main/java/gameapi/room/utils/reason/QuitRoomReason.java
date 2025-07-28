package gameapi.room.utils.reason;

/**
 * @author glorydark
 * <p>
 * This aims at targeting the real cause of player leave.
 */
public enum QuitRoomReason {

    DEFAULT,
    PLAYER_OFFLINE,
    PLAYER_LEAVE,
    ALLOCATE_ERROR,
    TELEPORT,
    ROOM_RESET,
    ROOM_UNLOAD,
    GAME_ERROR
}
