package gameapi.locker;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import gameapi.GameAPI;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author glorydark
 * @date {2024/1/2} {11:00}
 */
@Data
@Builder
public class LockerEntry {

    String name;

    LockerEntryType entryType;

    String identifier;

    int particleData;

    // entity refers to victim or projectile
    public void use(Player player) {
        switch (entryType) {
            case SOUND:
                player.getLevel().addSound(player, Arrays.stream(Sound.values()).filter(sound -> sound.getSound().equals(identifier)).findAny().orElseGet(new Supplier<Sound>() {
                    @Override
                    public Sound get() {
                        GameAPI.plugin.getLogger().warning("Cannot find sound with identifier [" + identifier + "]!");
                        return Sound.NOTE_HARP;
                    }
                }));
            case DEATH_CRATE:
                

            case KILL_EFFECT:

                break;
            case WALKING_PARTICLE:
                if (Particle.particleExists(identifier)) {
                    player.getLevel().addParticle(new GenericParticle(player.getLocation(), Particle.getParticleIdByName(identifier), particleData));
                }
                break;
        }
    }

    public void use(Entity entity) {
        if (entryType == LockerEntryType.PROJECTILE_TRAIL) {
            if (Particle.particleExists(identifier)) {
                entity.getLevel().addParticle(new GenericParticle(entity.getLocation(), Particle.getParticleIdByName(identifier), particleData));
            }
        }
    }
}
