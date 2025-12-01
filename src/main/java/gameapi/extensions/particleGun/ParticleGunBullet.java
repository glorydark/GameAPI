package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import gameapi.event.extra.EntityDamageByEntityByGunEvent;
import gameapi.extensions.particleGun.data.PlayerGunDataStorage;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.tools.CollisionBoxTools;
import gameapi.tools.EntityTools;
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
    protected boolean alive = true;
    private Vector3 currentPos;
    private int passedLayerTick = 0;
    private Vector3 lastUpdatePos;
    private int maxPassedLayerTick = 5;
    private List<BlockVector3> passedBlockList = new ArrayList<>();
    private List<Entity> hitEntities = new ArrayList<>();

    private float bulletSpeed = 3f;

    private Vector3 moveDirection;

    public ParticleGunBullet(Player owner, ParticleGun gun) {
        this.startPos = owner.add(0, owner.getEyeHeight(), 0);
        this.startPos.setComponents(calculateOffsetPosition(
                this.startPos,
                owner.getYaw(), owner.getPitch(),
                0, 0.025, -0.025));
        this.currentPos = this.startPos.clone();
        this.lastUpdatePos = this.currentPos.clone();
        this.owner = owner;
        this.gun = gun;

        double yawRad = Math.toRadians(this.startPos.getYaw());
        double pitchRad = Math.toRadians(this.startPos.getPitch());

        // 计算方向向量
        double dirX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double dirY = -Math.sin(pitchRad);
        double dirZ = Math.cos(yawRad) * Math.cos(pitchRad);

        // 归一化
        double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (length > 0) {
            dirX /= length;
            dirY /= length;
            dirZ /= length;
        }
        this.moveDirection = new Vector3(dirX , dirY, dirZ);
    }

    public void move(double speed) {
        // 应用速度
        this.currentPos = this.currentPos.add(
                new Vector3(
                        this.moveDirection.getX() * speed,
                        this.moveDirection.getY() * speed,
                        this.moveDirection.getZ() * speed
                )
        );
    }

    public void onBulletMove() {
        if (this.passedLayerTick > this.maxPassedLayerTick) {
            this.alive = false;
            return;
        }

        this.lastUpdatePos = this.currentPos.clone();
        this.move(this.getGun().getBulletMoveSpeed());

        String customIdentifier = this.gun.getCustomBulletParticle();

        int pointId = 1;

        for (Vector3 vector3 : getPointsAtInterval(this.lastUpdatePos, this.currentPos, 0.02)) {
            if (vector3.distance(this.startPos) > this.getGun().getBulletMoveDist()) {
                this.alive = false;
                return;
            }
            PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.getShootingStorageOrCreate(this.owner);
            if (playerGunDataStorage.isShowBulletParticle()) {
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
                    this.collide(entity, vector3, getBulletAttackPos(vector3, entity));
                    this.hitEntities.add(entity);
                    this.passedLayerTick += 3;
                } else {
                    if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getHorizontal(entity), entity.getYaw(), vector3)) {
                        this.collide(entity, vector3, EntityDamageByEntityByGunEvent.AttackPos.ARM);
                        this.hitEntities.add(entity);
                        this.passedLayerTick += 3;
                        //System.out.println("horizontal");
                    } else if (CollisionBoxTools.intersectsPoint(CollisionBoxTools.getVertical(entity), entity.getYaw(), vector3)) {
                        this.collide(entity, vector3, vector3.getY() >= entity.getEyeHeight()? EntityDamageByEntityByGunEvent.AttackPos.HEAD: EntityDamageByEntityByGunEvent.AttackPos.CHEST);
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

    public void collide(Entity entity, Vector3 hitPos, EntityDamageByEntityByGunEvent.AttackPos attackPos) {
        float damage = this.getBulletDamage(attackPos);
        if (this.getGun().isDamageEntity()) {
            if (entity == this.getOwner() || !entity.isAlive()) {
                return;
            }
            if (entity instanceof Player player) {
                if (!this.getGun().isDamagePlayer()) {
                    return;
                }
                if (player.isCreative() || player.isSpectator()) {
                    return;
                }
            }
            EntityDamageByEntityByGunEvent event = new EntityDamageByEntityByGunEvent(this.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, this.getGun().getItem(false), attackPos);
            event.setKnockBack(0f);
            event.setAttackCooldown(0);
            entity.attack(event);
            EntityTools.bigJump(entity, 1.2, 0.1, true);
        } else {
            if (this.getGun().isDamagePlayer()) {
                if (entity == this.getOwner() || !entity.isPlayer || !entity.isAlive()) {
                    return;
                }
                Player entityPlayer = (Player) entity;
                if (entityPlayer.isCreative() || entityPlayer.isSpectator()) {
                    return;
                }
                boolean crit = hitPos.getY() > entity.getEyeHeight() + entity.getY();
                EntityDamageByEntityByGunEvent event = new EntityDamageByEntityByGunEvent(this.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, this.getGun().getItem(false), attackPos);
                if (this.getPassedLayerTick() > 0) {
                    event.setDamage(event.getDamage() * ((float) this.getPassedLayerTick() / this.getMaxPassedLayerTick()));
                }
                if (crit) {
                    event.setDamage(event.getDamage() * 1.5f);
                    this.getOwner().getLevel().addSound(this.getOwner(), Sound.NOTE_HARP, 1.0f, 1.0f, this.getOwner());
                    // this.owner.sendMessage("crit!");
                } else {
                    this.getOwner().getLevel().addSound(this.getOwner(), Sound.NOTE_BASS, 1.0f, 1.0f, this.getOwner());
                }
                event.setKnockBack(0f);
                event.setAttackCooldown(0);
                entity.attack(event);
                EntityTools.bigJump(entity, this.getGun().getBulletKnockback(this.getOwner()), 0.1, true);
            }
        }
    }

    public Vector3 calculateOffsetPosition(Vector3 ownerPos, double yaw, double pitch, double forwardOffset, double horizontalOffset, double verticalOffset) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // 计算前方向量（基于yaw和pitch）
        double forwardX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double forwardY = -Math.sin(pitchRad);
        double forwardZ = Math.cos(yawRad) * Math.cos(pitchRad);

        // 计算右方向量（前方向量绕Y轴旋转90度）
        double rightX = -Math.sin(yawRad + Math.PI / 2);
        double rightY = 0;
        double rightZ = Math.cos(yawRad + Math.PI / 2);

        // 计算上方向量（前方向量和右方向量的叉积）
        double upX = forwardY * rightZ - forwardZ * rightY;
        double upY = forwardZ * rightX - forwardX * rightZ;
        double upZ = forwardX * rightY - forwardY * rightX;

        // 归一化所有方向向量
        normalizeVector(forwardX, forwardY, forwardZ);
        normalizeVector(rightX, rightY, rightZ);
        normalizeVector(upX, upY, upZ);

        // 计算最终位置：眼睛位置 + 前向偏移 + 水平偏移 + 垂直偏移
        double startX = ownerPos.getX() + forwardX * forwardOffset + rightX * horizontalOffset + upX * verticalOffset;
        double startY = ownerPos.getY() + forwardY * forwardOffset + rightY * horizontalOffset + upY * verticalOffset;
        double startZ = ownerPos.getZ() + forwardZ * forwardOffset + rightZ * horizontalOffset + upZ * verticalOffset;

        return new Vector3(startX, startY, startZ);
    }

    private void normalizeVector(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length > 0) {
            x /= length;
            y /= length;
            z /= length;
        }
    }
}
