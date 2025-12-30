package gameapi.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntitySnowball;
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

    protected float gravity = 0.03F;
    protected ParticleEffect particleEffect;

    private EntityBulletSnowball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    public static void launch(Player player,
                              Vector3 directionVector,
                              float gravity,
                              float motionMultiply,
                              ParticleEffect particleEffect, Consumer<CompoundTag> tagConsumer) {
        CompoundTag nbt = (new CompoundTag())
                .putList((new ListTag<>("Pos"))
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + (double)player.getEyeHeight() - 0.30000000149011613D))
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
        bulletSnowBall.setParticleEffect(particleEffect);
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

    public void setParticleEffect(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean onUpdate = super.onUpdate(currentTick);
        if (this.particleEffect != null && !this.closed &&
                !this.onGround && !this.hadCollision && currentTick %5 == 0) {
            this.level.addParticleEffect(this, this.particleEffect);
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
}
