package gameapi.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.*;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class EntityBulletSnowball extends EntitySnowball {

    protected float gravity = 0F;
    protected Consumer<EntityBulletSnowball> particleEffectConsumer;

    public EntityBulletSnowball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    public static void launch(Player player,
                              Vector3 directionVector,
                              float gravity,
                              float motionMultiply,
                              ParticleEffect particleEffect, Consumer<CompoundTag> tagConsumer) {
        launch(player, directionVector,
                gravity, motionMultiply,
                snowball -> snowball.level.addParticleEffect(snowball, particleEffect), tagConsumer);
    }

    public static void launch(Player player,
                              Vector3 directionVector,
                              float gravity,
                              float motionMultiply,
                              Consumer<EntityBulletSnowball> particleEffectConsumer, Consumer<CompoundTag> tagConsumer) {
        CompoundTag nbt = (new CompoundTag())
                .putList((new ListTag<>("Pos"))
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + (double)player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList((new ListTag<>("Motion"))
                        .add(new DoubleTag("", directionVector.x))
                        .add(new DoubleTag("", directionVector.y))
                        .add(new DoubleTag("", directionVector.z)))
                .putList((new ListTag<>("Rotation"))
                        .add(new FloatTag("", (float)player.yaw))
                        .add(new FloatTag("", (float)player.pitch)));
        if (tagConsumer != null) {
            tagConsumer.accept(nbt);
        }
        EntityBulletSnowball bulletSnowBall = new EntityBulletSnowball(player.getChunk(), nbt, player);
        bulletSnowBall.setGravity(gravity);
        bulletSnowBall.setParticleEffectConsumer(particleEffectConsumer);
        bulletSnowBall.setMotion(bulletSnowBall.getMotion().multiply(motionMultiply));
        bulletSnowBall.spawnToAll();
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }


    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    protected float getDrag() {
        return 0F;
    }

    public void setParticleEffectConsumer(Consumer<EntityBulletSnowball> particleEffectConsumer) {
        this.particleEffectConsumer = particleEffectConsumer;
    }

    public Consumer<EntityBulletSnowball> getParticleEffectConsumer() {
        return particleEffectConsumer;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.age > 10000L) {
            this.close();
            return false;
        }
        boolean onUpdate = super.onUpdate(currentTick);
        if (this.particleEffectConsumer != null && !this.closed &&
                !this.onGround && !this.hadCollision && currentTick %5 == 0) {
            this.particleEffectConsumer.accept(this);
        }
        return onUpdate;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity == this.shootingEntity) {
            return false;
        }
        return super.canCollideWith(entity);
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        ProjectileHitEvent hitEvent = new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity));
        this.server.getPluginManager().callEvent(hitEvent);
        if (!hitEvent.isCancelled()) {
            float damage = this instanceof EntitySnowball && entity instanceof EntityBlaze ? 3.0F : (float)this.getResultDamage();
            EntityDamageByEntityEvent ev;
            if (this.shootingEntity == null) {
                ev = new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.PROJECTILE, damage);
            } else {
                ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, EntityDamageEvent.DamageCause.PROJECTILE, damage);
            }
            ev.setAttackCooldown(0);

            if (entity.attack(ev)) {
                this.hadCollision = true;
                this.onHit();
                if (this.fireTicks > 0) {
                    EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
                    this.server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        entity.setOnFire(event.getDuration());
                    }
                }
            }

            this.close();
        }
    }

    @Override
    protected void updateMotion() {
        this.motionX *= 1.0F - this.getDrag();
        this.motionZ *= 1.0F - this.getDrag();
    }

    @Override
    public String getName() {
        return "EntityBulletSnowball";
    }
}
