package gameapi.room.utils.reason;

/**
 * @author glorydark
 *
 * This aims at targeting the real cause of player leave.
 */
public enum QuitRoomReason {

    DEFAULT,
    PLAYER_OFFLINE,
    PLAYER_OPERATION,
    GAME_ERROR,
    TELEPORT
}
