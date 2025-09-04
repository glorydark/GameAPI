package gameapi.manager.extension;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.utils.TextFormat;
import gameapi.annotation.Description;
import gameapi.extensions.particleGun.ParticleGun;
import gameapi.extensions.particleGun.data.PlayerGunData;
import gameapi.extensions.particleGun.data.PlayerGunDataStorage;
import gameapi.extensions.particleGun.task.ParticleGunUpdateTask;
import gameapi.room.task.EasyTask;

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
public class ParticleGunManager implements Listener {

    public static Map<Player, PlayerGunDataStorage> PARTICLE_GUN_USING_CACHES = new LinkedHashMap<>();

    private static final Map<String, PlayerGunData> playerParticleGunDataMap = new LinkedHashMap<>();

    private static final Map<String, ParticleGun> REGISTERED_PARTICLE_GUNS = new LinkedHashMap<>();

    public static ScheduledExecutorService EXECUTOR = null;

    public static List<EasyTask> TASK_QUEUES = new ArrayList<>();

    public static void disable() {
        if (EXECUTOR != null) {
            EXECUTOR.shutdown();
            for (EasyTask taskQueue : TASK_QUEUES) {
                taskQueue.cancel();
            }
            TASK_QUEUES.clear();
        }
    }

    public static void init() {
        disable();
        for (EasyTask taskQueue : TASK_QUEUES) {
            taskQueue.cancel();
        }
        PARTICLE_GUN_USING_CACHES.clear();
        EXECUTOR = Executors.newScheduledThreadPool(4);
        TASK_QUEUES.add(new ParticleGunUpdateTask());
        EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                for (EasyTask taskQueue : new ArrayList<>(TASK_QUEUES)) {
                    if (!taskQueue.isCancelled()) {
                        taskQueue.onRun(Server.getInstance().getTick());
                    } else {
                        TASK_QUEUES.remove(taskQueue);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public static void startAutoShooting(Player player) {
        if (PARTICLE_GUN_USING_CACHES.containsKey(player)) {
            PlayerGunDataStorage playerGunDataStorage = PARTICLE_GUN_USING_CACHES.get(player);
            if (System.currentTimeMillis() - playerGunDataStorage.getLastChangeStateMillis() > 50L) {
                playerGunDataStorage.setShooting(true);
                playerGunDataStorage.setLastChangeStateMillis(System.currentTimeMillis());
                player.sendMessage(TextFormat.GREEN + "已开启自动射击！");
            }
        } else {
            PlayerGunDataStorage playerGunDataStorage = new PlayerGunDataStorage();
            playerGunDataStorage.setShooting(true);
            PARTICLE_GUN_USING_CACHES.put(player, playerGunDataStorage);
        }
    }

    public static void stopAutoShooting(Player player) {
        if (PARTICLE_GUN_USING_CACHES.containsKey(player)) {
            PlayerGunDataStorage playerGunDataStorage = PARTICLE_GUN_USING_CACHES.get(player);
            if (System.currentTimeMillis() - playerGunDataStorage.getLastChangeStateMillis() > 50L) {
                Item item = player.getInventory().getItemInHand();
                if (item.hasCompoundTag()) {
                    ParticleGun gun = REGISTERED_PARTICLE_GUNS.get(item.getNamedTag().getString("gameapi:gun"));
                    if (gun != null) {
                        if (gun.isAutoShoot()) {
                            playerGunDataStorage.setShooting(false);
                            playerGunDataStorage.setLastChangeStateMillis(System.currentTimeMillis());
                            player.sendMessage(TextFormat.RED + "已关闭自动射击！");
                        }
                    }
                }
            }
        } else {
            PlayerGunDataStorage playerGunDataStorage = new PlayerGunDataStorage();
            playerGunDataStorage.setShooting(false);
            PARTICLE_GUN_USING_CACHES.put(player, playerGunDataStorage);
        }
    }

    public static boolean isShooting(Player player) {
        return PARTICLE_GUN_USING_CACHES.containsKey(player) && PARTICLE_GUN_USING_CACHES.get(player).isShooting();
    }

    public static boolean isReloading(Player player) {
        return PARTICLE_GUN_USING_CACHES.containsKey(player) && PARTICLE_GUN_USING_CACHES.get(player).isReloading();
    }

    @Description(usage = "You'd better use this method to remove player cache after he leaves the gun game in avoidance of unstoppable task checking.")
    public static void removeShootingStorage(Player player) {
        PARTICLE_GUN_USING_CACHES.remove(player);
    }

    public static PlayerGunDataStorage getShootingStorage(Player player) {
        return PARTICLE_GUN_USING_CACHES.getOrDefault(player, new PlayerGunDataStorage());
    }

    public static void registerParticleGun(ParticleGun gun) {
        REGISTERED_PARTICLE_GUNS.put(gun.getIdentifier(), gun);
    }

    public static ParticleGun getParticleGun(String identifier) {
        return REGISTERED_PARTICLE_GUNS.get(identifier);
    }

    public static Map<String, PlayerGunData> getPlayerParticleGunDataMap() {
        return playerParticleGunDataMap;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        int heldIndex = player.getInventory().getHeldItemIndex();
        Item item = player.getInventory().getItemInHand();
        if (item.hasCompoundTag()) {
            ParticleGun gun = REGISTERED_PARTICLE_GUNS.get(item.getNamedTag().getString("gameapi:gun"));
            if (gun != null) {
                PlayerGunDataStorage playerGunDataStorage = PARTICLE_GUN_USING_CACHES.getOrDefault(player, new PlayerGunDataStorage());
                if (!playerGunDataStorage.isReloading()) {
                    playerGunDataStorage.reload(player, item, heldIndex, gun);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = player.getInventory().getItemInHand();
        if (item.hasCompoundTag()) {
            ParticleGun gun = REGISTERED_PARTICLE_GUNS.get(item.getNamedTag().getString("gameapi:gun"));
            if (gun == null) {
                return;
            }
            String particleGunId = gun.getIdentifier() + "_" + item.getNamedTag().getInt("id");
            PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.PARTICLE_GUN_USING_CACHES.computeIfAbsent(player, k -> new PlayerGunDataStorage());
            ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
            PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
            if (playerGunData.getMaxAmmo() == 0) {
                return;
            }
            if (gun.isAutoShoot()) {
                if (isShooting(player)) {
                    stopAutoShooting(player);
                } else {
                    startAutoShooting(player);
                }
            } else {
                if (playerGunDataStorage.isReloading()) {
                    return;
                }
                if (System.currentTimeMillis() - playerGunDataStorage.getLastShootInterval() >= gun.getShootInterval()) {
                    playerGunDataStorage.setLastShootInterval(System.currentTimeMillis());
                    playerGunDataStorage.shoot(player, item, gun);
                }
            }
        }
    }

    @EventHandler
    public void onPacketReceive(DataPacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (event.getPacket() instanceof InventoryTransactionPacket packet) {
            if (!player.spawned || !player.isAlive()) {
                return;
            }
            if (player.isSpectator()) {
                return;
            }
            switch (packet.transactionType) {
                case InventoryTransactionPacket.TYPE_USE_ITEM, InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                    Item item = player.getInventory().getItemInHand();
                    if (item.hasCompoundTag()) {
                        ParticleGun gun = REGISTERED_PARTICLE_GUNS.get(item.getNamedTag().getString("gameapi:gun"));
                        if (gun == null) {
                            return;
                        }
                        String particleGunId = gun.getIdentifier() + "_" + item.getNamedTag().getInt("id");
                        PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.PARTICLE_GUN_USING_CACHES.computeIfAbsent(player, k -> new PlayerGunDataStorage());
                        ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
                        PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
                        if (playerGunData.getMaxAmmo() == 0) {
                            return;
                        }
                        if (gun.isAutoShoot()) {
                            if (isShooting(player)) {
                                stopAutoShooting(player);
                            } else {
                                startAutoShooting(player);
                            }
                        } else {
                            if (System.currentTimeMillis() - playerGunDataStorage.getLastShootInterval() >= gun.getShootInterval()) {
                                playerGunDataStorage.setLastShootInterval(System.currentTimeMillis());
                                playerGunDataStorage.shoot(player, item, gun);
                            }
                        }
                        event.setCancelled(true);
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.getAnimationType() == AnimatePacket.Action.SWING_ARM) {
            Player player = event.getPlayer();
            Item item = player.getInventory().getItemInHand();
            if (item.hasCompoundTag()) {
                ParticleGun gun = REGISTERED_PARTICLE_GUNS.get(item.getNamedTag().getString("gameapi:gun"));
                if (gun == null) {
                    return;
                }
                String particleGunId = gun.getIdentifier() + "_" + item.getNamedTag().getInt("id");
                PlayerGunDataStorage playerGunDataStorage = ParticleGunManager.PARTICLE_GUN_USING_CACHES.computeIfAbsent(player, k -> new PlayerGunDataStorage());
                ParticleGunManager.getPlayerParticleGunDataMap().computeIfAbsent(particleGunId, s -> new PlayerGunData(gun.getAmmo(), gun.getMaxAmmo()));
                PlayerGunData playerGunData = ParticleGunManager.getPlayerParticleGunDataMap().get(particleGunId);
                if (playerGunData.getMaxAmmo() == 0) {
                    return;
                }
                event.setCancelled(true);
                if (gun.isAutoShoot()) {
                    if (isShooting(player)) {
                        stopAutoShooting(player);
                    } else {
                        startAutoShooting(player);
                    }
                } else {
                    if (System.currentTimeMillis() - playerGunDataStorage.getLastShootInterval() >= gun.getShootInterval()) {
                        playerGunDataStorage.setLastShootInterval(System.currentTimeMillis());
                        playerGunDataStorage.shoot(player, item, gun);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PARTICLE_GUN_USING_CACHES.remove(event.getPlayer());
    }
}
