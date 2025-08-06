package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.room.task.EasyTask;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class ParticleGun {

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

    private boolean autoShoot;

    // This aims at marking each item with unique ids
    // for caching particle gun data with map instead of saving in item nbt.
    private static int ID = 1;

    public void shoot(Player player) {
        ParticleGunBullet particleGunBullet = new ParticleGunBullet(player, this);
        ParticleGunManager.TASK_QUEUES.add(new EasyTask() {
            @Override
            public void onRun(int tick) {
                if (particleGunBullet.isAlive()) {
                    particleGunBullet.onBulletMove();
                } else {
                    this.cancel();
                }
            }
        });
        this.onShoot(player);
    }

    public Item getItem() {
        Item item1 = this.item.clone();
        CompoundTag tag = new CompoundTag();
        if (item1.hasCompoundTag()) {
            tag = item1.getNamedTag();
        }
        tag.putString("gameapi:gun", this.identifier);
        // tag.putInt("ammo", this.getAmmo());
        // tag.putInt("max_ammo", this.getMaxAmmo());
        tag.putInt("id", ID);
        ID++;
        item1.setNamedTag(tag);
        item1.setLore(this.getDescription());
        item1.setCustomName(this.getName());
        item1.setUnbreakable(true);
        return item1;
    }

    public void onReload(Player player) {

    }

    public void onShoot(Player player) {

    }
}
