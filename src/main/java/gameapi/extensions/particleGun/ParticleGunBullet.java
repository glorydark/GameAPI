package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import gameapi.extensions.particleGun.entity.ParticleGunFakeBullet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
@Data
public class ParticleGunBullet {

    private final Player owner;

    private final ParticleGun gun;

    private final Location startPos;
    private final Vector3 movingMotion;
    protected boolean alive = true;
    private Vector3 currentPos;
    private int passedLayerTick = 0;
    private ParticleGunFakeBullet entityArmorStand;
    private int age = 0;
    private Vector3 lastUpdatePos;

    private float bulletSpeed = 3f;

    public ParticleGunBullet(Player owner, ParticleGun gun) {
        this.startPos = owner.add(0, 1.25, 0);
        this.currentPos = this.startPos.clone();
        this.lastUpdatePos = this.currentPos.clone();
        this.owner = owner;
        this.gun = gun;
        this.entityArmorStand = new ParticleGunFakeBullet(this.owner.getChunk(), EntityArmorStand.getDefaultNBT(owner.add(0, owner.getEyeHeight(), 0)), this);
        this.entityArmorStand.noClip = true;
        this.entityArmorStand.spawnToAll();
        double yawRad = Math.toRadians(owner.getYaw());
        double pitchRad = Math.toRadians(owner.getPitch());

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        this.movingMotion = new Vector3(x, y, z);
    }

    public void onBulletMove() {
        if (!this.entityArmorStand.isAlive() || this.entityArmorStand.isClosed()) {
            this.alive = false;
            this.entityArmorStand.close();
            return;
        }
        if (this.age > 100) {
            this.alive = false;
            this.entityArmorStand.close();
            return;
        }
        this.age++;
        this.entityArmorStand.move(this.movingMotion.getX() * this.bulletSpeed, this.movingMotion.getY() * this.bulletSpeed, this.movingMotion.getZ() * this.bulletSpeed);
        this.entityArmorStand.updateMovement();

        if (this.entityArmorStand.distance(this.startPos) > this.gun.getBulletMoveDist()) {
            this.alive = false;
            this.entityArmorStand.close();
        } else {
            Particle particle = this.gun.getShootParticle();
            particle.setComponents(Location.fromObject(this.entityArmorStand, owner.getLevel()));
            this.owner.getLevel().addParticle(particle);
            /*
            for (Entity entity : this.owner.getLevel().getEntities()) {
                if (entity.getBoundingBox().isVectorInside(this.entityArmorStand)) {
                    this.entityArmorStand.collide(entity);
                }
            }
             */
            List<Entity> hitEntities = new ArrayList<>();

            for (Vector3 vector3 : getPointsAtInterval(this.lastUpdatePos, this.entityArmorStand, 0.3)) {
                Particle particle1 = this.gun.getShootParticle();

                particle1.setComponents(Location.fromObject(vector3, owner.getLevel()));
                this.owner.getLevel().addParticle(particle1);

                for (Entity entity : this.owner.getLevel().getEntities()) {
                    if (hitEntities.contains(entity)) {
                        continue;
                    }
                    if (entity.getBoundingBox().isVectorInside(vector3)) {
                        this.entityArmorStand.collide(entity);
                        hitEntities.add(entity);
                    }
                }
            }
        }
        this.lastUpdatePos = this.entityArmorStand.clone();
    }

    public static List<Vector3> getPointsAtInterval(Vector3 start, Vector3 end, double distance) {
        List<Vector3> points = new ArrayList<>();

        Vector3 direction = end.subtract(start);
        double length = direction.length();

        if (length == 0) {
            points.add(start);
            return points;
        }

        Vector3 unitDirection = direction.multiply(1.0 / length);

        double currentDistance = 0;
        while (currentDistance <= length) {
            Vector3 point = start.add(unitDirection.multiply(currentDistance));
            points.add(point);
            currentDistance += distance;
        }

        // 确保最后一个点是终点
        if (!points.get(points.size() - 1).equals(end)) {
            points.add(end);
        }

        return points;
    }
}
