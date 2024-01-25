package gameapi.extensions.locker.entry;

import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.PlaySoundPacket;
import gameapi.annotation.Future;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/4} {22:04}
 */
@Future
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

    @Override
    public void use(Entity entity) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = identifier;
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = entity.getFloorX();
        packet.y = entity.getFloorY();
        packet.z = entity.getFloorZ();
        entity.getLevel().addChunkPacket(entity.getFloorX() >> 4, entity.getFloorZ() >> 4, packet);
    }
}
