package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import gameapi.event.extra.EntityDamageByEntityByGunEvent;
import gameapi.extensions.particleGun.entity.ParticleGunFakeBullet;
import gameapi.tools.CollisionBoxTools;
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
    private int maxPassedLayerTick = 5;
    private List<BlockVector3> passedBlockList = new ArrayList<>();
    private List<Entity> hitEntities = new ArrayList<>();

    private float bulletSpeed = 3f;

    public ParticleGunBullet(Player owner, ParticleGun gun) {
        this.startPos = owner.add(0, owner.getEyeHeight(), 0);
        this.currentPos = this.startPos.clone();
        this.lastUpdatePos = this.currentPos.clone();
        this.owner = owner;
        this.gun = gun;
        this.entityArmorStand = new ParticleGunFakeBullet(this.owner.getChunk(), EntityArmorStand.getDefaultNBT(this.startPos), this, this.gun.getItem(false));
        this.entityArmorStand.noClip = true;
        this.entityArmorStand.setPosition(this.startPos);
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
        if (this.age > 1200) {
            this.alive = false;
            this.entityArmorStand.close();
            return;
        }
        if (this.passedLayerTick > this.maxPassedLayerTick) {
            this.alive = false;
            this.entityArmorStand.close();
            return;
        }
        this.entityArmorStand.onGround = true;
        this.age++;
        this.entityArmorStand.move(this.movingMotion.getX() * this.bulletSpeed, this.movingMotion.getY() * this.bulletSpeed, this.movingMotion.getZ() * this.bulletSpeed);
        this.entityArmorStand.updateMovement();
        if (this.entityArmorStand.distance(this.startPos) > this.gun.getBulletMoveDist()) {
            this.alive = false;
            this.entityArmorStand.close();
        } else {
            String customIdentifier = this.gun.getCustomBulletParticle();
            if (customIdentifier.isEmpty()) {
                Particle particle = this.gun.getVanillaBulletParticle();
                particle.setComponents(Location.fromObject(this.entityArmorStand, owner.getLevel()));
                this.owner.getLevel().addParticle(particle);
            } else {
                SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
                pk.uniqueEntityId = -1L;
                pk.dimensionId = this.owner.getLevel().getDimension();
                pk.identifier = customIdentifier;
                pk.position = this.entityArmorStand.asVector3f();
                Server.broadcastPacket(this.owner.getLevel().getPlayers().values(), pk);
            }
            /*
            for (Entity entity : this.owner.getLevel().getEntities()) {
                if (entity.getBoundingBox().isVectorInside(this.entityArmorStand)) {
                    this.entityArmorStand.collide(entity);
                }
            }
             */

            int pointId = 1;
            for (Vector3 vector3 : getPointsAtInterval(this.lastUpdatePos, this.entityArmorStand, 0.02)) {
                if (pointId % 5 == 0) {
                    if (customIdentifier.isEmpty()) {
                        Particle particle1 = this.gun.getVanillaBulletParticle();
                        if (particle1 != null) {
                            particle1.setComponents(Location.fromObject(vector3, owner.getLevel()));
                            this.owner.getLevel().addParticle(particle1);
                        }
                    } else {
                        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
                        pk.uniqueEntityId = -1L;
                        pk.dimensionId = this.owner.getLevel().getDimension();
                        pk.identifier = customIdentifier;
                        pk.position = vector3.asVector3f();
                        Server.broadcastPacket(this.owner.getLevel().getPlayers().values(), pk);
                    }
                }

                if (!this.passedBlockList.contains(vector3.asBlockVector3())) {
                    Block block = this.owner.getLevel().getBlock(vector3);
                    if (block.isSolid() &&
                            block.getBoundingBox() != null
                            && block.getBoundingBox().isVectorInside(vector3)) {
                        this.passedLayerTick += 2;
                        this.passedBlockList.add(block.asBlockVector3());
                    }
                }

                for (Entity entity : this.owner.getLevel().getEntities()) {
                    if (!entity.isPlayer || entity == this.owner) {
                        continue;
                    }
                    if (this.hitEntities.contains(entity)) {
                        continue;
                    }
                    if (entity.getBoundingBox().isVectorInside(vector3)) {
                        this.entityArmorStand.collide(entity, getBulletAttackPos(vector3, entity));
                        this.hitEntities.add(entity);
                        this.passedLayerTick += 3;
                    } else {
                        if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getHorizontal(entity), entity.getYaw(), vector3)) {
                            this.entityArmorStand.collide(entity, EntityDamageByEntityByGunEvent.AttackPos.ARM);
                            this.hitEntities.add(entity);
                            this.passedLayerTick += 3;
                            //System.out.println("horizontal");
                        } else if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getVertical(entity), entity.getYaw(), vector3)) {
                            this.entityArmorStand.collide(entity, vector3.getY() >= entity.getEyeHeight()? EntityDamageByEntityByGunEvent.AttackPos.HEAD: EntityDamageByEntityByGunEvent.AttackPos.CHEST);
                            this.hitEntities.add(entity);
                            this.passedLayerTick += 3;
                            //System.out.println("vertical");
                        }
                        /*
                        if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getHorizontal(entity), entity.getYaw(), vector3)) {
                            this.entityArmorStand.collide(entity, getBulletAttackPos(vector3, entity));
                            this.hitEntities.add(entity);
                            this.passedLayerTick += 3;
                            System.out.println("horizontal");
                        } else if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getVertical(entity), entity.getYaw(), vector3)) {
                            this.entityArmorStand.collide(entity, getBulletAttackPos(vector3, entity));
                            this.hitEntities.add(entity);
                            this.passedLayerTick += 3;
                            System.out.println("vertical");
                        }
                         */
                    }
                }
                pointId++;
            }
        }
        this.lastUpdatePos = this.entityArmorStand.clone();
    }

    public float getBulletDamage(EntityDamageByEntityByGunEvent.AttackPos pos) {
        return switch (pos) {
            case HEAD -> this.getGun().getBulletDamageHead();
            case CHEST -> this.getGun().getBulletDamageChest();
            case LEG -> this.getGun().getBulletDamageLeg();
            case ARM -> this.getGun().getBulletDamageArm();
        };
    }

    public EntityDamageByEntityByGunEvent.AttackPos getBulletAttackPos(Vector3 bulletPos, Entity entity) {
        double base = entity.getY();
        if (bulletPos.getY() >= entity.getEyeHeight() + base - 0.2) {
            return EntityDamageByEntityByGunEvent.AttackPos.HEAD;
        } else if (bulletPos.getY() > base + (entity.isShortSneaking()? 0.88f: 1.26f)) {
            return EntityDamageByEntityByGunEvent.AttackPos.CHEST;
        } else {
            return EntityDamageByEntityByGunEvent.AttackPos.LEG;
        }
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
