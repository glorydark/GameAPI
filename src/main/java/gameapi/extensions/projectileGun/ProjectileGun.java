package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.entity.EntityBulletSnowball;
import lombok.Data;

import java.util.UUID;

/**
 * @author glorydark
 */
@Data
public abstract class ProjectileGun {

    private final String identifier;

    private final String name;

    private final int ammo;

    private final double bulletSpeed;

    private final int reloadTicks;

    private final int shootInterval;

    private final float bulletDamage;

    public ProjectileGun(String identifier, String name, int ammo, double bulletSpeed, int reloadTicks, int shootInterval, float bulletDamage) {
        this.identifier = identifier;
        this.name = name;
        this.ammo = ammo;
        this.bulletSpeed = bulletSpeed;
        this.reloadTicks = reloadTicks;
        this.shootInterval = shootInterval;
        this.bulletDamage = bulletDamage;
    }

    public void shoot(Player player) {
        EntityBulletSnowball.launch(
                player,
                player.getDirectionVector(),
                0.03f,
                (float) this.bulletSpeed,
                ParticleEffect.CHERRY_LEAVES_PARTICLE,
                compoundTag -> compoundTag.putFloat("gameapi:gun_bullet_damage", getBulletDamage()));
    }

    public abstract Item getItem(Player player);

    public Item toWeaponItem(Player player) {
        Item item = this.getItem(player);
        CompoundTag compoundTag = new CompoundTag();
        if (item.hasCompoundTag()) {
            compoundTag = item.getNamedTag();
        }
        compoundTag.putString("gameapi:projectile_gun", this.identifier);
        compoundTag.putString("gameapi:gun_uuid", UUID.randomUUID().toString());
        item.setNamedTag(compoundTag);
        item.setCount(this.getAmmo());
        return item;
    }
}
