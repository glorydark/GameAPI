package gameapi.locker.entry;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.PlaySoundPacket;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/4} {22:04}
 */
@Getter
public class LockerSoundEntry extends LockerEntry {

    String identifier;

    float volume;

    float pitch;

    LockerSoundEntry(String name, String identifier, float volume, float pitch) {
        super(name);
        this.identifier = identifier;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void use(Player player) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = identifier;
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = player.getFloorX();
        packet.y = player.getFloorY();
        packet.z = player.getFloorZ();
        player.getLevel().addChunkPacket(player.getFloorX() >> 4, player.getFloorZ() >> 4, packet);
    }
}
