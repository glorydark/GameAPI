package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.room.task.EasyTask;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class ParticleGun {

    private final String identifier;
    private final String name;
    private final String description;
    private final Item item;

    private final int ammo;
    private final int maxAmmo;

    private final Particle vanillaBulletParticle;
    private final String customBulletParticle;

    private final int reloadTick;
    private final long shootInterval;
    private final float moveSpeedMultiplier;

    private final int bulletMoveDist;
    private final double bulletMoveSpeed;

    private final float bulletDamageHead;
    private final float bulletDamageChest;
    private final float bulletDamageLeg;
    private final float bulletDamageArm;

    private double bulletKnockback;

    private boolean damageEntity;
    private boolean damagePlayer;

    private boolean autoShoot;

    // This aims at marking each item with unique ids
    // for caching particle gun data with map instead of saving in item nbt.
    private static int ID = 1;

    @Deprecated
    public ParticleGun(String identifier, String name, String description, Item item,
                       int ammo, int maxAmmo, Particle vanillaBulletParticle, String customBulletParticle,
                       int reloadTick, int shootInterval, float moveSpeedMultiplier, double bulletKnockback,
                       int bulletMoveDist, double bulletMoveSpeed,
                       float bulletDamageHead, float bulletDamageChest, float bulletDamageLeg, float bulletDamageArm,
                       boolean damageEntity, boolean damagePlayer, boolean autoShoot) {

        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.item = item;
        this.ammo = ammo;
        this.maxAmmo = maxAmmo;
        this.vanillaBulletParticle = vanillaBulletParticle;
        this.customBulletParticle = customBulletParticle;
        this.reloadTick = reloadTick;
        this.shootInterval = shootInterval;
        this.moveSpeedMultiplier = moveSpeedMultiplier;
        this.bulletMoveDist = bulletMoveDist;
        this.bulletMoveSpeed = bulletMoveSpeed;
        this.bulletDamageHead = bulletDamageHead;
        this.bulletDamageChest = bulletDamageChest;
        this.bulletDamageLeg = bulletDamageLeg;
        this.bulletDamageArm = bulletDamageArm;
        this.damageEntity = damageEntity;
        this.damagePlayer = damagePlayer;
        this.autoShoot = autoShoot;
        this.bulletKnockback = bulletKnockback;
    }

    public ParticleGun(String identifier, String name, String description, Item item,
                       int ammo, int maxAmmo, String customBulletParticle,
                       int reloadTick, int shootInterval, float moveSpeedMultiplier, double bulletKnockback,
                       int bulletMoveDist, double bulletMoveSpeed,
                       float bulletDamageHead, float bulletDamageChest, float bulletDamageLeg, float bulletDamageArm,
                       boolean damageEntity, boolean damagePlayer, boolean autoShoot) {
        this(identifier, name, description,
                item, ammo, maxAmmo,
                null, customBulletParticle, reloadTick,
                shootInterval, moveSpeedMultiplier, bulletKnockback, bulletMoveDist, bulletMoveSpeed,
                bulletDamageHead, bulletDamageChest, bulletDamageLeg, bulletDamageArm,
                damageEntity, damagePlayer, autoShoot);
    }

    public ParticleGun(String identifier, String name, String description, Item item,
                       int ammo, int maxAmmo, Particle vanillaBulletParticle,
                       int reloadTick, int shootInterval, float moveSpeedMultiplier, double bulletKnockback,
                       int bulletMoveDist, double bulletMoveSpeed,
                       float bulletDamageHead, float bulletDamageChest, float bulletDamageLeg, float bulletDamageArm,
                       boolean damageEntity, boolean damagePlayer, boolean autoShoot) {
        this(identifier, name, description,
                item, ammo, maxAmmo,
                vanillaBulletParticle, "", reloadTick,
                shootInterval, moveSpeedMultiplier, bulletKnockback, bulletMoveDist, bulletMoveSpeed,
                bulletDamageHead, bulletDamageChest, bulletDamageLeg, bulletDamageArm,
                damageEntity, damagePlayer, autoShoot);
    }

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
        return this.getItem(true);
    }

    public Item getItem(boolean createId) {
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

    public double getBulletKnockback(Player player) {
        return this.getBulletKnockback();
    }
}
