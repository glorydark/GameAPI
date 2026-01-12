package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;
import gameapi.items.ProjectileGunInteractableItem;
import gameapi.tools.SmartTools;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author glorydark
 */
public class ProjectileGunReloadTask extends Task {

    public final Player player;
    public final ProjectileGunInteractableItem item;

    private static final String filledChar = String.valueOf('▏');
    private static final String emptyChar = String.valueOf('▏');
    private static final String colored = "§e";
    private static final String uncolored = "§7";

    public ProjectileGunReloadTask(Player player, ProjectileGunInteractableItem item) {
        this.player = player;
        this.item = item;
    }

    public CompoundTag getProjectileGunData(Item item) {
        return item.hasCompoundTag()? (item.getNamedTag().contains("gameapi:projectile_gun_data")? item.getNamedTag().getCompound("gameapi:projectile_gun_data"): null): null;
    }

    @Override
    public void onRun(int i) {
        if (!this.player.isOnline() || this.player.getInventory() == null) {
            ProjectileGunInteractableItem.reloadPlayers.remove(this.player.getName());
            this.cancel();
            return;
        }
        Item currentItem = this.player.getInventory().getItemInHand();
        if (currentItem.hasCompoundTag()) {
            CompoundTag tag = this.getProjectileGunData(currentItem);
            if (tag != null) {
                String currentItemUuid = currentItem.getNamedTag().getString("gameapi:gun_uuid");
                final String uuid = this.item.getNamedTag().getString("gameapi:gun_uuid");
                int reloadTicks = tag.getInt("reloadTicks");
                double reloadTickPerTick = (double) this.item.getMaxDurability() / reloadTicks;
                if (uuid.equals(currentItemUuid)) {
                    int nextDamage = new BigDecimal(currentItem.getDamage() - reloadTickPerTick).setScale(1, RoundingMode.UP).intValue();
                    boolean finishReplenish = false;
                    if (nextDamage <= 0) {
                        nextDamage = 0;
                        finishReplenish = true;
                        this.player.sendActionBar("§a换弹完毕");
                    } else {
                        this.player.sendActionBar("§a换弹中 " + SmartTools.getCountdownProgressBar(this.item.getMaxDurability() - nextDamage, this.item.getMaxDurability(), 20));
                    }
                    currentItem.setDamage(nextDamage);
                    if (finishReplenish) {
                        currentItem.setCount(tag.getInt("ammo"));
                        ProjectileGunInteractableItem.reloadPlayers.remove(this.player.getName());
                        this.cancel();
                    }
                    this.player.getInventory().setItemInHand(currentItem);
                } else {
                    ProjectileGunInteractableItem.reloadPlayers.remove(this.player.getName());
                    this.cancel();
                }
            }
        }
    }
}
