package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.utils.Animation;
import gameapi.utils.protocol.AnimateEntityPacketV2;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 * @date {2023/12/29} {21:18}
 */
public class EntityTools {

    public static void knockBack(Entity attacker, Entity victim) {
        knockBack(attacker, victim, 0.4, false);
    }

    public static void knockBack(Entity attacker, Entity victim, double base) {
        knockBack(attacker, victim, base, false);
    }

    public static void knockBack(Entity attacker, Entity victim, boolean reverse) {
        knockBack(attacker, victim, 0.4, reverse);
    }

    public static void knockBack(Entity attacker, Entity victim, double base, boolean directionReverse) {
        double x = victim.getX() - attacker.getX();
        double z = victim.getZ() - attacker.getZ();
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1.0 / f;

        Vector3 motion = new Vector3(victim.motionX, victim.motionY, victim.motionZ);

        if (directionReverse) {
            x = -x;
            z = -z;
        }

        motion.x /= 2.0d;
        motion.y /= 2.0d;
        motion.z /= 2.0d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        victim.setMotion(motion);
    }

    public static void knockBackV2(Entity attacker, Entity victim, double base, double XzKB, double yKB) {
        double x = victim.getX() - attacker.getX();
        double z = victim.getZ() - attacker.getZ();
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1.0 / f;

        Vector3 motion = new Vector3(victim.motionX, victim.motionY, victim.motionZ);

        motion.x /= 2.0d;
        motion.y /= 2.0d;
        motion.z /= 2.0d;
        motion.x += x * f * base * XzKB;
        motion.y += base * yKB;
        motion.z += z * f * base * XzKB;

        if (motion.y > base) {
            motion.y = base;
        }

        victim.setMotion(motion);
    }

    public static void dropExpOrb(Location source, int exp) {
        if (source != null && source.getChunk() != null) {
            Random rand = ThreadLocalRandom.current();
            for (int split : EntityXPOrb.splitIntoOrbSizes(exp)) {
                CompoundTag nbt = Entity.getDefaultNBT(source, new Vector3((rand.nextDouble() * 0.2 - 0.1) * 2.0, rand.nextDouble() * 0.4, (rand.nextDouble() * 0.2 - 0.1) * 2.0), rand.nextFloat() * 360.0F, 0.0F);
                nbt.putShort("Value", split);
                nbt.putShort("PickupDelay", 10);
                nbt.putBoolean("AntiClean", true);
                Entity entity = Entity.createEntity("XpOrb", source.getChunk(), nbt);
                if (entity != null) {
                    entity.spawnToAll();
                }
            }
        }
    }

    public static void playAnimation(Entity entity, Animation animation) {
        playAnimation(entity, animation, entity.getViewers().values().toArray(new Player[0]));
    }

    public static void playAnimation(Entity entity, Animation animation, Player viewer) {
        playAnimation(entity, animation, new Player[]{viewer});
    }

    public static void playAnimation(Entity entity, Animation animation, Player[] viewers) {
        AnimateEntityPacketV2 pk = AnimateEntityPacketV2.fromAnimation(animation);
        pk.addAnimatedEntityRuntimeIds(entity.getId());
        Server.broadcastPacket(viewers, pk);
    }
}
