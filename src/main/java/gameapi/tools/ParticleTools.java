package gameapi.tools;

import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;

/**
 * @author glorydark
 */
public class ParticleTools {

    public static void drawCircle(Particle particle, Location location, double horizontalRadius) {
        for (int angle = 1; angle <= 360; angle++) {
            if (angle % 30 == 0) {
                double x1 = location.getX() + horizontalRadius * Math.cos(angle * 3.14 / 180);
                double z1 = location.getZ() + horizontalRadius * Math.sin(angle * 3.14 / 180);
                Particle particle_temp = (Particle) particle.clone();
                particle_temp.setX(x1);
                particle_temp.setY(location.getY());
                particle_temp.setZ(z1);
                location.getLevel().addParticle(particle_temp);
            }
        }
    }
}
