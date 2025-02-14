package gameapi.room.utils.reason;

/**
 * @author glorydark
 *
 * This aims at targeting the real cause of wrong room reset.
 */
public enum ResetAllReason {

    DEFAULT,
    ROOM_AUTO_DESTROY,
    ROOM_GAME_FINISH,
    ROOM_PLAYBACK_LEAVE,
    NO_ENOUGH_PLAYERS,
    NO_ENOUGH_TEAM,
    ERROR
}
