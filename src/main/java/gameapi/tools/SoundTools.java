package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;

/**
 * @author Glorydark
 */
public class SoundTools {

    public static void stopAllSound(Player player) {
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = " ";
        pk.stopAll = true;
        player.dataPacket(pk);
    }

    /**
     * @param player : 玩家
     * @description: 停止播放材质包音乐
     */
    public static void stopResourcePackOggSound(Player player, String sound) {
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = sound;
        pk.stopAll = false;
        player.dataPacket(pk);
    }

    public static void addSoundToPlayer(Player player, Sound sound, float volume, float pitch) {
        addSoundToPlayer(player, sound.getSound(), volume, pitch);
    }

    /**
     * @param player    : 玩家
     * @param filename: 材质包中sound_definition的项的名称
     * @description: 播放材质包音乐
     */
    public static void addSoundToPlayer(Player player, String filename, float volume, float pitch) {
        PlaySoundPacket pk = new PlaySoundPacket();
        pk.name = filename;
        pk.x = player.getFloorX();
        pk.y = player.getFloorY();
        pk.z = player.getFloorZ();
        pk.volume = volume;
        pk.pitch = pitch;
        player.dataPacket(pk);
    }

    public static void addAmbientSound(Position position, Sound sound) {
        position.getLevel().addSound(position, sound);
    }
}
