package gameapi.sound;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;

public class Sound {
    /**
     * @description: 停止播放材质包音乐
     * @param player : 玩家
     */
    public static void stopResourcePackOggSound(Player player){
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = " ";
        pk.stopAll = true;
        player.dataPacket(pk);
    }

    /**
     * @description: 播放材质包音乐
     * @param player : 玩家
     * @param filename: 材质包中sound_defination的项的名称
     */
    public static void playResourcePackOggMusic(Player player, String filename){
        PlaySoundPacket pk = new PlaySoundPacket();
        pk.name = filename;
        pk.x = (int) player.x;
        pk.y = (int) player.y;
        pk.z = (int) player.z;
        pk.volume = (float) 1.0;
        pk.pitch = (float) 1.0;
        player.dataPacket(pk);
    }

    public static void addAmbientSound(Level level, Player player, cn.nukkit.level.Sound sound){
        level.addSound(player.getPosition(),sound);
    }
}
