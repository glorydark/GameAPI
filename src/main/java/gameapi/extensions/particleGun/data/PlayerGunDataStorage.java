package gameapi.extensions.particleGun.data;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import gameapi.extensions.particleGun.ParticleGun;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.room.task.EasyTask;
import gameapi.tools.SmartTools;

/**
 * @author glorydark
 */
public class PlayerGunDataStorage {

    protected boolean shooting = false;

    protected boolean reloading = false;

    protected long invalidateBeforeMillis = -1L;

    protected long lastChangeStateMillis = -1L;

    protected long lastInteractMillis = -1L;

    public PlayerGunDataStorage() {

    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public long getLastInteractMillis() {
        return lastInteractMillis;
    }

    public void setLastInteractMillis(long lastInteractMillis) {
        this.lastInteractMillis = lastInteractMillis;
    }

    public long getInvalidateBeforeMillis() {
        return invalidateBeforeMillis;
    }

    public void setInvalidateBeforeMillis(long invalidateBeforeMillis) {
        this.invalidateBeforeMillis = invalidateBeforeMillis;
    }

    public long getLastChangeStateMillis() {
        return lastChangeStateMillis;
    }

    public void setLastChangeStateMillis(long lastChangeStateMillis) {
        this.lastChangeStateMillis = lastChangeStateMillis;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public void reload(Player player, Item item, int heldIndex, ParticleGun gun) {
        if (this.isReloading()) {
            return;
        }
        CompoundTag tag = item.getNamedTag();
        String particleGunId = gun.getIdentifier() + "_" + tag.getInt("id");
        ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
        PlayerGunData startPlayerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
        if (startPlayerGunData.getMaxAmmo() <= 0) {
            player.sendMessage(TextFormat.RED + "弹匣已空！");
            return;
        }
        if (startPlayerGunData.getAmmo() == gun.getAmmo()) {
            return;
        }
        this.setReloading(true);
        gun.onReload(player);
        player.sendMessage("开始换弹!");
        final long startMillis = System.currentTimeMillis();

        PlayerGunDataStorage playerGunDataStorage = this;
        ParticleGunManager.TASK_QUEUES.add(new EasyTask() {
            int tick = 0;

            @Override
            public void onRun(int i) {
                if (!player.isOnline() || player.getInventory() == null) {
                    playerGunDataStorage.setReloading(false);
                    this.cancel();
                    return;
                }
                if (!player.getInventory().getItemInHand().equals(item) || heldIndex != player.getInventory().getHeldItemIndex()) {
                    player.sendMessage(TextFormat.RED + "换弹中断！");
                    playerGunDataStorage.setReloading(false);
                    this.cancel();
                    return;
                }
                PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
                int ammo = playerGunData.getAmmo();
                int maxAmmo = playerGunData.getMaxAmmo();
                if (playerGunDataStorage.getInvalidateBeforeMillis() < startMillis) {
                    if (this.tick < gun.getReloadTick()) {
                        tick++;
                        player.sendTip(TextFormat.GREEN + "Ammo: " + ammo + "/" + maxAmmo + "\n" + TextFormat.RED + "Reloading " + SmartTools.getCountdownProgressBar(tick, gun.getReloadTick(), 40, "§e", "§7", "▏", "▏") + " " + SmartTools.tickToSecondString(gun.getReloadTick() - this.tick, 1) + "s");
                    } else {
                        playerGunData.setMaxAmmo(maxAmmo - (gun.getAmmo() - ammo));
                        playerGunData.setAmmo(gun.getAmmo());
                        // tag.putInt("max_ammo", maxAmmo - weapon.getAmmo());
                        // tag.putInt("ammo", weapon.getAmmo());
                        // item.setNamedTag(tag);
                        // player.getInventory().setItemInHand(item);
                        playerGunDataStorage.setReloading(false);
                        player.sendMessage(TextFormat.GREEN + "换弹完毕!");
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        });
    }

    public void shoot(Player player, Item item, ParticleGun gun) {
        String particleGunId = gun.getIdentifier() + "_" + item.getNamedTag().getInt("id");
        this.shoot(player, particleGunId, gun);
    }

    public void shoot(Player player, String particleGunId, ParticleGun gun) {
        if (this.isReloading()) {
            return;
        }
        ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
        PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
        if (playerGunData.getAmmo() > 0) {
            playerGunData.setAmmo(playerGunData.getAmmo() - 1);
            gun.shoot(player);
        }
    }
}
