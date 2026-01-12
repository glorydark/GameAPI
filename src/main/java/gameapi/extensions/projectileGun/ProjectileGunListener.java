package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.item.Item;
import gameapi.entity.EntityBulletSnowball;
import gameapi.items.ProjectileGunInteractableItem;

/**
 * @author glorydark
 */
public class ProjectileGunListener implements Listener {

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
            Item item = player.getInventory().getItemInHand();
            if (item.hasCompoundTag() && item.getNamedTag().contains("gameapi:projectile_gun")) {
                item.onUse(player, 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ProjectileGunInteractableItem.lastShootTickMap.remove(event.getPlayer().getName());
        ProjectileGunInteractableItem.reloadPlayers.remove(event.getPlayer().getName());
    }
}
