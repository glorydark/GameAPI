package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;

/**
 * @author glorydark
 */
public class VanillaCustomMusicTools {

    public static final int EVENT_QUEUE_CUSTOM_MUSIC = 1900;
    public static final int EVENT_PLAY_CUSTOM_MUSIC = 1901;
    public static final int EVENT_STOP_CUSTOM_MUSIC = 1902;
    public static final int EVENT_SET_MUSIC_VOLUME = 1903;

    // repeat mode -> 0 loop, 1 play-once
    public static void playCustomMusic(String trackName, float volume, boolean repeatMode, float fadeSeconds, Player... players) {
        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = EVENT_PLAY_CUSTOM_MUSIC;
        pk.tag = new CompoundTag()
                .putFloat("volume", volume)
                .putFloat("fadeSeconds", fadeSeconds)
                .putBoolean("repeatMode", repeatMode)
                .putFloat("fadeSeconds", 1.0f);
        Server.broadcastPacket(players, pk);
    }

    public static void queueCustomMusic(String trackName, float volume, boolean repeatMode, float fadeSeconds, Player... players) {
        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = EVENT_QUEUE_CUSTOM_MUSIC;
        pk.tag = new CompoundTag()
                .putString("trackName", trackName)
                .putFloat("volume", volume)
                .putFloat("fadeSeconds", fadeSeconds)
                .putBoolean("repeatMode", repeatMode)
                .putFloat("fadeSeconds", 1.0f);
        Server.broadcastPacket(players, pk);
    }

    public static void setCustomMusicVolume(float volume, Player... players) {
        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = EVENT_SET_MUSIC_VOLUME;
        pk.tag = new CompoundTag().putFloat("volume", volume);
        Server.broadcastPacket(players, pk);
    }

    public static void stopCustomMusic(float fadeSeconds, Player... players) {
        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = EVENT_STOP_CUSTOM_MUSIC;
        pk.tag = new CompoundTag().putFloat("fadeSeconds", fadeSeconds);
        Server.broadcastPacket(players, pk);
    }
}
