package gameapi.extensions.particleGun.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import gameapi.extensions.particleGun.ParticleGun;
import gameapi.extensions.particleGun.data.PlayerGunData;
import gameapi.extensions.particleGun.data.PlayerGunDataStorage;
import gameapi.manager.extension.ParticleGunManager;
import gameapi.room.task.EasyTask;
import gameapi.tools.DecimalTools;
import gameapi.tools.PlayerTools;

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
                PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.PARTICLE_GUN_USING_CACHES.computeIfAbsent(player, k -> new PlayerGunDataStorage());
                CompoundTag tag = item.getNamedTag();
                ParticleGun gun = ParticleGunManager.getParticleGun(tag.getString("gameapi:gun"));
                if (gun == null) {
                    if (!playerGunDataStorage.getLastUpdateWeaponSpeedId().isEmpty()) {
                        PlayerTools.resetSpeed(player, 0.1f);
                        playerGunDataStorage.setLastUpdateWeaponSpeedId("");
                    }
                    continue;
                }
                if (!playerGunDataStorage.getLastUpdateWeaponSpeedId().equals(gun.getIdentifier())) {
                    float modifiedSpeed = gun.getMoveSpeedMultiplier() * 0.1f;
                    modifiedSpeed = DecimalTools.getFloat(modifiedSpeed, 1);
                    PlayerTools.resetSpeed(player, modifiedSpeed);
                    playerGunDataStorage.setLastUpdateWeaponSpeedId(gun.getIdentifier());
                }
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
