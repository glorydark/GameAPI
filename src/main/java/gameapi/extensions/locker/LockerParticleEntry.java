package gameapi.extensions.locker;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import gameapi.annotation.Future;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/4} {22:14}
 */
@Future
@Getter
public class LockerParticleEntry extends LockerEntry {

    private final String identifier;

    private final int extraData;

    public LockerParticleEntry(String name, String identifier, int extraData) {
        super(name);
        this.identifier = identifier;
        this.extraData = extraData;
    }

    @Override
    public void use(Entity entity) {
        entity.getLevel().addParticle(new GenericParticle(entity.getLocation(), Particle.getParticleIdByName(identifier), extraData));
    }
}
