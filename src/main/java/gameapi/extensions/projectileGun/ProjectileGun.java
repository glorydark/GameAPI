package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
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
    private final float bulletSpeed;
    private final int reloadTicks;
    private final int shootInterval;
    private final float bulletDamage;
    private static final CompoundTag DEFAULT_BULLET_PARTICLE_TAG;
    static {
        BlockColor yellow = BlockColor.YELLOW_BLOCK_COLOR;
        DEFAULT_BULLET_PARTICLE_TAG = new CompoundTag()
                .putCompound("variable.color",
                        new CompoundTag()
                            .putFloat("r", yellow.getRed() / 255F)
                            .putFloat("g", yellow.getGreen() / 255F)
                            .putFloat("b", yellow.getBlue() / 255F))
                .putString("id", "minecraft:falling_dust");
    }
    private CompoundTag bulletParticleTag = DEFAULT_BULLET_PARTICLE_TAG;

    public ProjectileGun(String identifier, String name, int ammo, float bulletSpeed, int reloadTicks, int shootInterval, float bulletDamage) {
        this.identifier = identifier;
        this.name = name;
        this.ammo = ammo;
        this.bulletSpeed = bulletSpeed;
        this.reloadTicks = reloadTicks;
        this.shootInterval = shootInterval;
        this.bulletDamage = bulletDamage;
    }

    public void setBulletParticleTag(CompoundTag bulletParticleTag) {
        this.bulletParticleTag = bulletParticleTag;
    }

    public CompoundTag getBulletParticleTag() {
        return bulletParticleTag;
    }

    public abstract Item getItem(Player player);

    public Item toWeaponItem(Player player) {
        Item item = this.getItem(player);
        item.setCustomName(this.name);
        CompoundTag compoundTag = new CompoundTag();
        if (item.hasCompoundTag()) {
            compoundTag = item.getNamedTag();
        }
        compoundTag.putCompound("gameapi:projectile_gun_data", this.toCompoundTag());
        compoundTag.putString("gameapi:projectile_gun", this.identifier);
        compoundTag.putString("gameapi:gun_uuid", UUID.randomUUID().toString());
        item.setNamedTag(compoundTag);
        item.setCount(this.getAmmo());
        return item;
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("identifier", this.identifier);
        tag.putString("name", this.name);
        tag.putInt("ammo", this.ammo);
        tag.putFloat("bulletSpeed", this.bulletSpeed);
        tag.putInt("reloadTicks", this.reloadTicks);
        tag.putInt("shootInterval", this.shootInterval);
        tag.putFloat("bulletDamage", this.bulletDamage);

        // 如果有粒子颜色等配置，也可以保存
        if (this.bulletParticleTag != null) {
            tag.putCompound("particleColor", this.bulletParticleTag);
        }

        return tag;
    }
}
