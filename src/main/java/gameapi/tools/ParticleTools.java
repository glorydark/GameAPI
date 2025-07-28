package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;

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

    public static void drawCircle(ParticleEffect particle, Location location, double horizontalRadius) {
        for (int angle = 1; angle <= 360; angle++) {
            if (angle % 30 == 0) {
                double x1 = location.getX() + horizontalRadius * Math.cos(angle * 3.14 / 180);
                double z1 = location.getZ() + horizontalRadius * Math.sin(angle * 3.14 / 180);
                Position position = location.clone();
                position.setX(x1);
                position.setY(location.getY());
                position.setZ(z1);
                location.getLevel().addParticleEffect(position, particle);
            }
        }
    }

    public static void drawCircle(String particleEffectId, Location location, double horizontalRadius, Player... players) {
        for (int angle = 1; angle <= 360; angle++) {
            if (angle % 30 == 0) {
                Level level = location.getLevel();
                double x1 = location.getX() + horizontalRadius * Math.cos(angle * 3.14 / 180);
                double z1 = location.getZ() + horizontalRadius * Math.sin(angle * 3.14 / 180);
                Vector3f pos = location.setX(x1).setZ(z1).asVector3f();
                SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
                pk.identifier = particleEffectId;
                pk.uniqueEntityId = -1L;
                pk.dimensionId = level.getDimension();
                pk.position = pos;
                if (players == null) {
                    level.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                } else {
                    Server.broadcastPacket(level.getPlayers().values(), pk);
                }
            }
        }
    }

    public static void lineSpace(String particleEffectId, Level level, SimpleAxisAlignedBB bb, Player... players) {
        double xStepDist = (bb.getMaxX() - bb.getMinX()) / 5;
        double yStepDist = (bb.getMaxY() - bb.getMinY()) / 5;
        double zStepDist = (bb.getMaxZ() - bb.getMinZ()) / 5;
        for (double x = bb.getMinX(); x <= bb.getMaxX(); x += xStepDist) {
            for (double y = bb.getMinY(); y <= bb.getMaxY(); y += yStepDist) {
                for (double z = bb.getMinZ(); z <= bb.getMaxZ(); z += zStepDist) {
                    addParticleEffect(particleEffectId, level, new Vector3(x, y, z).asVector3f());
                }
            }
        }
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMaxZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMinZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMinZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMaxZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMinX(), bb.getMinY(), bb.getMaxZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMinZ()).asVector3f());
        addParticleEffect(particleEffectId, level, new Vector3(bb.getMinX(), bb.getMinY(), bb.getMinZ()).asVector3f());
    }

    public static void addParticleEffect(String particleEffectId, Level level, Vector3f vector3f, Player... players) {
        level.addParticleEffect(vector3f, particleEffectId, -1, level.getDimension(), players);
    }

    public static void addParticleEffect(String particleEffectId, Level level, Vector3 vector3, Player... players) {
        addParticleEffect(particleEffectId, level, vector3.asVector3f(), players);
    }

    public static void addParticleEffect(String particleEffectId, Position position, Player... players) {
        addParticleEffect(particleEffectId, position.getLevel(), position, players);
    }
}
