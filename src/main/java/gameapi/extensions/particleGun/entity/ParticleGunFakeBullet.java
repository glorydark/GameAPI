package gameapi.extensions.particleGun.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.extensions.particleGun.ParticleGunBullet;

/**
 * @author glorydark
 */
public class ParticleGunFakeBullet extends EntityProjectile {

    public ParticleGunBullet particleGunBullet;

    public ParticleGunFakeBullet(FullChunk chunk, CompoundTag nbt, ParticleGunBullet particleGunBullet) {
        super(chunk, nbt);
        this.particleGunBullet = particleGunBullet;
    }

    protected void initEntity() {
        super.initEntity();
        this.getDataProperties().putLong(0, 65536L);
        this.setScale(0f);
    }

    public void collide(Entity entity) {
        if (this.particleGunBullet.getGun().isDamageEntity()) {
            if (entity == this.particleGunBullet.getOwner() || !entity.isAlive()) {
                return;
            }
            if (entity instanceof Player player) {
                if (!this.particleGunBullet.getGun().isDamagePlayer()) {
                    return;
                }
                if (player.isCreative() || player.isSpectator()) {
                    return;
                }
            }
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this.particleGunBullet.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, this.particleGunBullet.getGun().getBulletDamage());
            event.setAttackCooldown(0);
            entity.attack(event);
        } else {
            if (this.particleGunBullet.getGun().isDamagePlayer()) {
                if (entity == this.particleGunBullet.getOwner() || !entity.isPlayer || !entity.isAlive()) {
                    return;
                }
                Player entityPlayer = (Player) entity;
                if (entityPlayer.isCreative() || entityPlayer.isSpectator()) {
                    return;
                }
                if (this.particleGunBullet.getOwner().getLevel().getBlock(this).isSolid()) {
                    this.particleGunBullet.setPassedLayerTick(this.particleGunBullet.getPassedLayerTick() + 1);
                }
                if (this.particleGunBullet.getPassedLayerTick() > 3) {
                    this.particleGunBullet.setAlive(false);
                    this.close();
                    return;
                }
                boolean crit = this.getY() > entity.getEyeHeight() + entity.getY();
                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this.particleGunBullet.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, particleGunBullet.getGun().getBulletDamage());
                if (this.particleGunBullet.getPassedLayerTick() > 0) {
                    event.setDamage(event.getDamage() * (this.particleGunBullet.getPassedLayerTick() / 5f));
                }
                if (crit) {
                    event.setDamage(event.getDamage() * 1.5f);
                    this.particleGunBullet.getOwner().getLevel().addSound(this.particleGunBullet.getOwner(), Sound.NOTE_HARP, 1.0f, 1.0f, this.particleGunBullet.getOwner());
                    // this.owner.sendMessage("crit!");
                } else {
                    this.particleGunBullet.getOwner().getLevel().addSound(this.particleGunBullet.getOwner(), Sound.NOTE_BASS, 1.0f, 1.0f, this.particleGunBullet.getOwner());
                }
                event.setAttackCooldown(0);
                entity.attack(event);
                this.close();
            }
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    protected float getDrag() {
        return 0f;
    }

    @Override
    protected float getGravity() {
        return 0f;
    }

    @Override
    public float getScale() {
        return 0.01f;
    }

    public int getNetworkId() {
        return EntityArmorStand.NETWORK_ID;
    }
}
