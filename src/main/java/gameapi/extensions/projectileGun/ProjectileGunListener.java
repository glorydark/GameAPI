package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.entity.EntityBulletSnowball;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author glorydark
 */
public class ProjectileGunListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = player.getInventory().getItemInHand();
        if (ProjectileGunManager.reloadPlayers.contains(player)) {
            return;
        }
        if (item.hasCompoundTag() && item.getNamedTag().contains("gameapi:projectile_gun")) {
            String id = item.getNamedTag().getString("gameapi:projectile_gun");
            if (id.isEmpty()) {
                return;
            }
            ProjectileGun gunItem = ProjectileGunManager.getProjectileGun(id);
            if (gunItem != null) {
                if (item.getDamage() != 0) {
                    return;
                }
                int lastShootTick = ProjectileGunManager.lastShootTick.getOrDefault(event.getPlayer(), 0);
                int currentTick = Server.getInstance().getTick();
                if (currentTick - lastShootTick >= gunItem.getShootInterval()) {
                    int count = item.getCount() - 1;
                    if (count < 1) {
                        count = 1;
                    }
                    item.setCount(count);
                    gunItem.shoot(player);
                    if (count == 1) {
                        item.setDamage(item.getMaxDurability() - 1);
                        player.getInventory().setItemInHand(item);
                        this.reload(player);
                    } else {
                        player.getInventory().setItemInHand(item);
                        ProjectileGunManager.lastShootTick.put(event.getPlayer(), currentTick);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByChildEntityEvent event) {
        if (event.getChild() instanceof EntityBulletSnowball snowball) {
            if (snowball.namedTag.contains("gameapi:gun_bullet_damage")) {
                float damage = snowball.namedTag.getFloat("gameapi:gun_bullet_damage");
                if (damage > 0) {
                    event.setDamage(damage);
                }
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
           this.reload(player);
        }
    }

    public void reload(Player player) {
        if (ProjectileGunManager.reloadPlayers.contains(player)) {
            return;
        }
        Item item = player.getInventory().getItemInHand();
        if (item.hasCompoundTag() && item.getNamedTag().contains("gameapi:projectile_gun")) {
            String id = item.getNamedTag().getString("gameapi:projectile_gun");
            if (id.isEmpty()) {
                return;
            }
            ProjectileGun gunItem = ProjectileGunManager.getProjectileGun(id);
            if (gunItem != null) {
                if (item.getCount() == gunItem.getAmmo()) {
                    return;
                }
                if (item.getCount() > 1) {
                    item.setCount(1);
                    item.setDamage(item.getMaxDurability() - 1);
                    player.getInventory().setItemInHand(item);
                }
                final String uuid = item.getNamedTag().getString("gameapi:gun_uuid");
                ProjectileGunManager.reloadPlayers.add(player);
                int reloadTicks = gunItem.getReloadTicks();
                double reloadTickPerTick = (double) item.getMaxDurability() / reloadTicks;
                Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.getInstance(), new Task() {
                    @Override
                    public void onRun(int i) {
                        Item currentItem = player.getInventory().getItemInHand();
                        if (currentItem.hasCompoundTag() && currentItem.getNamedTag().contains("gameapi:gun_uuid")) {
                            String currentItemUuid = currentItem.getNamedTag().getString("gameapi:gun_uuid");
                            if (uuid.equals(currentItemUuid)) {
                                int nextDamage = new BigDecimal(currentItem.getDamage() - reloadTickPerTick).setScale(1, RoundingMode.UP).intValue();
                                boolean finishReplenish = false;
                                if (nextDamage <= 0) {
                                    nextDamage = 0;
                                    finishReplenish = true;
                                }
                                currentItem.setDamage(nextDamage);
                                if (finishReplenish) {
                                    currentItem.setCount(gunItem.getAmmo());
                                    ProjectileGunManager.reloadPlayers.remove(player);
                                    this.cancel();
                                }
                                player.getInventory().setItemInHand(currentItem);
                            } else {
                                ProjectileGunManager.reloadPlayers.remove(player);
                                this.cancel();
                            }
                        }
                    }
                }, 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ProjectileGunManager.lastShootTick.remove(event.getPlayer());
        ProjectileGunManager.reloadPlayers.remove(event.getPlayer());
    }
}
