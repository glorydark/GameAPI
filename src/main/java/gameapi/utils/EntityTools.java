package gameapi.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

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

        f = 1 / f;

        Vector3 motion = new Vector3(victim.motionX, victim.motionY, victim.motionZ);

        if (directionReverse) {
            x = -x;
            z = -z;
        }

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        victim.setMotion(motion);
    }
}
