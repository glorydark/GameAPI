package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.NukkitRunnable;
import gameapi.GameAPI;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class Weapon {

    private final String identifier;

    private final String name;

    private final String description;

    private final Item item;

    private final int ammo;

    private final int maxAmmo;

    private final Particle shootParticle;

    private final int reloadTick;

    private final int shootInterval;

    private final double moveSpeedBonus;

    private final int bulletMoveDist;

    private final double bulletMoveSpeed;

    private final float bulletDamage;

    private boolean damageEntity;

    private boolean damagePlayer;

    public void shoot(Player player) {
        Bullet bullet = new Bullet(player, this);
        new NukkitRunnable() {
            @Override
            public void run() {
                if (bullet.isAlive()) {
                    bullet.onBulletMove();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(GameAPI.getInstance(), 0, 1);
    }

    public Item getItem() {
        Item item1 = this.item.clone();
        CompoundTag tag = new CompoundTag();
        if (item1.hasCompoundTag()) {
            tag = item1.getNamedTag();
        }
        tag.putString("gameapi:gun", this.identifier);
        tag.putInt("ammo", this.getAmmo());
        tag.putInt("max_ammo", this.getMaxAmmo());
        item1.setNamedTag(tag);
        item1.setLore(this.getDescription());
        item1.setCustomName(this.getName());
        item1.setUnbreakable(true);
        return item1;
    }
}
