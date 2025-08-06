package gameapi.extensions.particleGun.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import gameapi.extensions.particleGun.ParticleGun;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.extensions.particleGun.data.PlayerGunData;
import gameapi.extensions.particleGun.data.PlayerGunDataStorage;
import gameapi.room.task.EasyTask;

/**
 * @author glorydark
 */
public class ParticleGunUpdateTask extends EasyTask {

    @Override
    public void onRun(int i) {
        try {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                if (!player.isOnline() || player.getInventory() == null) {
                    ParticleGunManager.PARTICLE_GUN_USING_CACHES.remove(player);
                    continue;
                }
                Item item = player.getInventory().getItemInHand();
                if (!item.hasCompoundTag()) {
                    continue;
                }
                CompoundTag tag = item.getNamedTag();
                ParticleGun gun = ParticleGunManager.getParticleGun(tag.getString("gameapi:gun"));
                if (gun == null) {
                    continue;
                }
                PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.PARTICLE_GUN_USING_CACHES.computeIfAbsent(player, k -> new PlayerGunDataStorage());
                String particleGunId = gun.getIdentifier() + "_" + tag.getInt("id");
                ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
                PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
                int ammo = playerGunData.getAmmo();
                int maxAmmo = playerGunData.getMaxAmmo();
                if (gun.isAutoShoot()) {
                    if (playerGunDataStorage.isShooting()) {
                        int heldIndex = player.getInventory().getHeldItemIndex();
                        // int ammo = tag.getInt("ammo");
                        // int maxAmmo = tag.getInt("max_ammo");
                        if (ammo <= 0) {
                            if (maxAmmo <= 0) {
                                playerGunDataStorage.setShooting(false);
                                player.sendMessage(TextFormat.RED + "弹匣已空！");
                            } else {
                                playerGunDataStorage.reload(player, item, heldIndex, gun);
                            }
                        } else {
                            if (playerGunDataStorage.isReloading()) {
                                return;
                            }
                            playerGunDataStorage.shoot(player, particleGunId, gun);
                            player.sendTip(TextFormat.GREEN + "Ammo: " + (ammo - 1) + "/" + maxAmmo);
                        }
                    } else {
                        player.sendTip(TextFormat.GREEN + "Ammo: " + ammo + "/" + maxAmmo);
                    }
                } else {
                    int heldIndex = player.getInventory().getHeldItemIndex();
                    if (ammo <= 0) {
                        playerGunDataStorage.reload(player, item, heldIndex, gun);
                    } else {
                        player.sendTip(TextFormat.GREEN + "Ammo: " + ammo + "/" + maxAmmo);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
