package gameapi.extensions.particleGun.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.event.extra.EntityDamageByEntityByGunEvent;
import gameapi.extensions.particleGun.ParticleGunBullet;
import gameapi.tools.EntityTools;

/**
 * @author glorydark
 */
public class ParticleGunFakeBullet extends EntityProjectile {

    public ParticleGunBullet particleGunBullet;

    public final Item item;

    public ParticleGunFakeBullet(FullChunk chunk, CompoundTag nbt, ParticleGunBullet particleGunBullet, Item item) {
        super(chunk, nbt);
        this.particleGunBullet = particleGunBullet;
        this.item = item;
    }

    protected void initEntity() {
        super.initEntity();
        this.getDataProperties().putLong(0, 65536L);
        this.setScale(0f);
    }

    public void collide(Entity entity, EntityDamageByEntityByGunEvent.AttackPos attackPos) {
        float damage = this.particleGunBullet.getBulletDamage(attackPos);
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
            EntityDamageByEntityByGunEvent event = new EntityDamageByEntityByGunEvent(this.particleGunBullet.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, this.particleGunBullet.getGun().getItem(false), attackPos);
            event.setKnockBack(0f);
            event.setAttackCooldown(0);
            entity.attack(event);
            EntityTools.bigJump(entity, 1.2, 0.1, true);
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
                boolean crit = this.getY() > entity.getEyeHeight() + entity.getY();
                EntityDamageByEntityByGunEvent event = new EntityDamageByEntityByGunEvent(this.particleGunBullet.getOwner(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, particleGunBullet.getGun().getItem(false), attackPos);
                if (this.particleGunBullet.getPassedLayerTick() > 0) {
                    event.setDamage(event.getDamage() * ((float) this.particleGunBullet.getPassedLayerTick() / this.particleGunBullet.getMaxPassedLayerTick()));
                }
                if (crit) {
                    event.setDamage(event.getDamage() * 1.5f);
                    this.particleGunBullet.getOwner().getLevel().addSound(this.particleGunBullet.getOwner(), Sound.NOTE_HARP, 1.0f, 1.0f, this.particleGunBullet.getOwner());
                    // this.owner.sendMessage("crit!");
                } else {
                    this.particleGunBullet.getOwner().getLevel().addSound(this.particleGunBullet.getOwner(), Sound.NOTE_BASS, 1.0f, 1.0f, this.particleGunBullet.getOwner());
                }
                event.setKnockBack(0f);
                event.setAttackCooldown(0);
                entity.attack(event);
                EntityTools.bigJump(entity, this.particleGunBullet.getGun().getBulletKnockback(this.particleGunBullet.getOwner()), 0.1, true);
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

    public Item getItem() {
        return item;
    }
}
