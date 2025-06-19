package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.extensions.particleGun.data.PlayerGunDataStorage;
import gameapi.tools.SmartTools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author glorydark
 */
public class WeaponManager implements Listener {

    public static Map<Player, PlayerGunDataStorage> WEAPON_USING_CACHES = new LinkedHashMap<>();

    public static Map<String, Weapon> REGISTERED_WEAPONS = new LinkedHashMap<>();

    public static ScheduledExecutorService EXECUTOR = null;

    public static List<Task> TASK_QUEUES = new ArrayList<>();

    public static void init() {
        if (EXECUTOR != null) {
            EXECUTOR.shutdownNow();
        }
        for (Task taskQueue : TASK_QUEUES) {
            taskQueue.cancel();
        }
        WEAPON_USING_CACHES.clear();
        TASK_QUEUES = new ArrayList<>();
        EXECUTOR = Executors.newScheduledThreadPool(4);
        EXECUTOR.scheduleAtFixedRate(WeaponManager::onStateUpdate, 0, 200, TimeUnit.MILLISECONDS);
        EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                for (Task taskQueue : new ArrayList<>(TASK_QUEUES)) {
                    if (taskQueue.getHandler() == null) {
                        taskQueue.setHandler(new TaskHandler(GameAPI.getInstance(), null, 0, false));
                    }
                    taskQueue.onRun(Server.getInstance().getTick());
                }
                TASK_QUEUES.removeIf(task -> task.getHandler().isCancelled());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    public static void startShooting(Player player) {
        if (WEAPON_USING_CACHES.containsKey(player)) {
            PlayerGunDataStorage playerGunDataStorage = WEAPON_USING_CACHES.get(player);
            if (System.currentTimeMillis() - playerGunDataStorage.getLastChangeStateMillis() > 300L) {
                playerGunDataStorage.setShooting(true);
                playerGunDataStorage.setLastChangeStateMillis(System.currentTimeMillis());
                player.sendMessage(TextFormat.GREEN + "已开启自动射击！");
            }
        } else {
            WEAPON_USING_CACHES.put(player, new PlayerGunDataStorage());
        }
    }

    public static void stopShooting(Player player) {
        PlayerGunDataStorage playerGunDataStorage = WEAPON_USING_CACHES.get(player);
        if (System.currentTimeMillis() - playerGunDataStorage.getLastChangeStateMillis() > 300L) {
            playerGunDataStorage.setShooting(false);
            playerGunDataStorage.setLastChangeStateMillis(System.currentTimeMillis());
            player.sendMessage(TextFormat.RED + "已关闭自动射击！");
        }
    }

    public static boolean isShooting(Player player) {
        return WEAPON_USING_CACHES.containsKey(player) && WEAPON_USING_CACHES.get(player).isShooting();
    }

    public static void removeShootingStorage(Player player) {
        WEAPON_USING_CACHES.remove(player);
    }

    public static PlayerGunDataStorage getShootingStorage(Player player) {
        return WEAPON_USING_CACHES.getOrDefault(player, new PlayerGunDataStorage());
    }

    protected static void onStateUpdate() {
        try {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                PlayerGunDataStorage playerGunDataStorage = WEAPON_USING_CACHES.get(player);
                Item item = player.getInventory().getItemInHand();
                if (item.hasCompoundTag()) {
                    CompoundTag tag = item.getNamedTag();
                    Weapon weapon = REGISTERED_WEAPONS.get(tag.getString("gameapi:gun"));
                    if (weapon != null) {
                        int ammo = tag.getInt("ammo");
                        int maxAmmo = tag.getInt("max_ammo");
                        if (playerGunDataStorage == null || !playerGunDataStorage.isReloading()) {
                            player.sendActionBar(TextFormat.GREEN + "Ammo: " + ammo + "/" + maxAmmo, 0, 1, 0);
                        }
                    }
                }
            }
            for (Map.Entry<Player, PlayerGunDataStorage> entry : WEAPON_USING_CACHES.entrySet()) {
                Player player = entry.getKey();
                PlayerGunDataStorage playerGunDataStorage = entry.getValue();
                if (!playerGunDataStorage.isShooting()) {
                    continue;
                }
                if (!player.isOnline() || player.getInventory() == null) {
                    WEAPON_USING_CACHES.remove(player);
                }
                Item item = player.getInventory().getItemInHand();
                int heldIndex = player.getInventory().getHeldItemIndex();
                if (item.hasCompoundTag()) {
                    CompoundTag tag = item.getNamedTag();
                    Weapon weapon = REGISTERED_WEAPONS.get(tag.getString("gameapi:gun"));
                    if (weapon != null) {
                        int ammo = tag.getInt("ammo");
                        int maxAmmo = tag.getInt("max_ammo");
                        if (ammo <= 0) {
                            if (maxAmmo <= 0) {
                                playerGunDataStorage.setShooting(false);
                                player.sendMessage(TextFormat.RED + "弹匣已空！");
                            } else {
                                final long startMillis = System.currentTimeMillis();
                                if (!playerGunDataStorage.isReloading()) {
                                    playerGunDataStorage.setReloading(true);
                                    player.sendMessage("开始换弹!");
                                    TASK_QUEUES.add(new Task() {
                                        int tick = 0;

                                        @Override
                                        public void onRun(int i) {
                                            if (!player.isOnline() || player.getInventory() == null) {
                                                this.cancel();
                                                return;
                                            }
                                            if (!player.getInventory().getItemInHand().equals(item) || heldIndex != player.getInventory().getHeldItemIndex()) {
                                                player.sendMessage(TextFormat.RED + "换弹中断！");
                                                playerGunDataStorage.setReloading(false);
                                                this.cancel();
                                                return;
                                            }
                                            if (playerGunDataStorage.getInvalidateBeforeMillis() < startMillis) {
                                                if (this.tick < weapon.getReloadTick()) {
                                                    tick++;
                                                    player.sendActionBar(TextFormat.GREEN + "Ammo: " + ammo + "/" + maxAmmo + "\n" + TextFormat.RED + "Reloading " + SmartTools.getCountdownProgressBar(tick, weapon.getReloadTick(), 40, "§e", "§7", "▏", "▏") + " " + SmartTools.tickToSecondString(weapon.getReloadTick() - this.tick, 1) + "s", 0, 1, 0);
                                                } else {
                                                    tag.putInt("max_ammo", maxAmmo - weapon.getAmmo());
                                                    tag.putInt("ammo", weapon.getAmmo());
                                                    item.setNamedTag(tag);
                                                    player.getInventory().setItemInHand(item);
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
                            }
                        } else {
                            tag.putInt("ammo", ammo - 1);
                            item.setNamedTag(tag);
                            player.getInventory().setItemInHand(item);
                            weapon.shoot(player);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            Item item = player.getInventory().getItemInHand();
            if (item.hasCompoundTag()) {
                Weapon weapon = REGISTERED_WEAPONS.get(item.getNamedTag().getString("gameapi:gun"));
                if (weapon != null) {
                    if (isShooting(player)) {
                        stopShooting(player);
                    } else {
                        startShooting(player);
                    }
                }
            }
        }
    }
}
