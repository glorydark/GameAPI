package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.MovePlayerPacket;
import gameapi.GameAPI;
import gameapi.commands.WorldEditCommand;
import gameapi.entity.GameProjectileEntity;
import gameapi.event.block.RoomBlockBreakEvent;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.entity.*;
import gameapi.event.inventory.RoomInventoryPickupItemEvent;
import gameapi.event.player.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.room.RoomVirtualHealthManager;
import gameapi.room.Room;
import gameapi.room.RoomChatData;
import gameapi.room.RoomStatus;
import gameapi.room.edit.EditProcess;
import gameapi.room.items.RoomItemBase;
import gameapi.room.team.BaseTeam;
import gameapi.tools.GameAPIComponentParser;
import gameapi.utils.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
public class BaseEventListener implements Listener {

    public static Map<String, List<DamageSource>> damageSources = new LinkedHashMap<>();

    public static Map<Long, EntityDamageSource> lastLivingEntityDamagedByEntitySources = new Long2ObjectOpenHashMap<>();

    public static Map<Long, PlayerDamageSource> lastLivingEntityDamagedByPlayerSources = new Long2ObjectOpenHashMap<>();

    public static void addPlayerDamageSource(String player, String damager) {
        List<DamageSource> temp = new ArrayList<>(damageSources.getOrDefault(player, new ArrayList<>()));
        DamageSource damageSource = new DamageSource(damager, System.currentTimeMillis());
        temp.add(damageSource);
        damageSources.put(player, temp);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
                room.sendMessageToAll(GameAPI.getLanguage().getTranslation("baseEvent.quit.success", player.getName()));
            if (room.getPlayers().contains(player)) {
                room.removePlayer(player);
            } else {
                room.removeSpectator(player);
            }
            player.setPosition(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
        } else {
            for (EditProcess editProcess : GameAPI.editProcessList) {
                Player editor = editProcess.getPlayer();
                if (editor == player) {
                    editProcess.onQuit();
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                event.setCancelled(true);
            }
            if (!room.getRoomRule().isAllowBreakBlock()) {
                if (!room.getRoomRule().getAllowBreakBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage()) && !room.getRoomRule().getAllowBreakBlocks().contains(event.getBlock().getId() + "")) {
                    event.setCancelled(true);
                } else {
                    RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, event.getBlock(), player, event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                    GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                    if (roomBlockBreakEvent.isCancelled()) {
                        event.setCancelled(true);
                    } else {
                        event.setDropExp(roomBlockBreakEvent.getDropExp());
                        event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                        event.setDrops(roomBlockBreakEvent.getDrops());
                    }
                }
            } else {
                RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, event.getBlock(), player, event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                if (roomBlockBreakEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.setDropExp(roomBlockBreakEvent.getDropExp());
                    event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                    event.setDrops(roomBlockBreakEvent.getDrops());
                }
            }
        } else {
            if (GameAPI.worldEditPlayers.contains(player)) {
                Block block = event.getBlock();
                if (block != null) {
                    if (GameAPI.worldEditPlayers.contains(player)) {
                        if (block.getId() != 0) {
                            player.sendMessage("方块: " + block.toItem().getNamespaceId() + ", 位置: " + block.getFloorX() + ":" + block.getFloorY() + ":" + block.getFloorZ());
                        }
                    }
                }
            }
            for (EditProcess editProcess : GameAPI.editProcessList) {
                Player editor = editProcess.getPlayer();
                if (editor == player) {
                    editProcess.getCurrentStep().onBreak(player, event.getBlock());
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (event.getItem() != null) {
                Item item = event.getItem();
                RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                if (roomItemBase != null) {
                    long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                    if (System.currentTimeMillis() >= nextUseMillis) {
                        roomItemBase.onBlockPlace(room, player, item);
                    }
                }
            }
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                event.setCancelled(true);
            } else {
                if (!room.getRoomRule().isAllowPlaceBlock()) {
                    if (!room.getRoomRule().getAllowPlaceBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage()) && !room.getRoomRule().getAllowPlaceBlocks().contains(event.getBlock().getId() + "")) {
                        event.setCancelled(true);
                    } else {
                        RoomBlockPlaceEvent roomBlockPlaceEvent = new RoomBlockPlaceEvent(room, event.getBlock(), player, event.getItem(), event.getBlockAgainst(), event.getBlockReplace());
                        GameListenerRegistry.callEvent(room, roomBlockPlaceEvent);
                        if (roomBlockPlaceEvent.isCancelled()) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    RoomBlockPlaceEvent roomBlockPlaceEvent = new RoomBlockPlaceEvent(room, event.getBlock(), player, event.getItem(), event.getBlockAgainst(), event.getBlockReplace());
                    GameListenerRegistry.callEvent(room, roomBlockPlaceEvent);
                    if (roomBlockPlaceEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (GameAPI.worldEditPlayers.contains(player)) {
                Block block = event.getBlock();
                if (block == null) {
                    return;
                }
                switch (block.getId()) {
                    case Block.REDSTONE_BLOCK:
                        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
                            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
                        }
                        WorldEditCommand.posSetLinkedHashMap.get(player).setPos1(block.getLocation());
                        player.sendMessage("Successfully set pos1 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                        event.setCancelled(true);
                        return;
                    case Block.EMERALD_BLOCK:
                        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
                            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
                        }
                        WorldEditCommand.posSetLinkedHashMap.get(player).setPos2(block.getLocation());
                        player.sendMessage("Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                        event.setCancelled(true);
                }
            } else {
                for (EditProcess editProcess : GameAPI.editProcessList) {
                    Player editor = editProcess.getPlayer();
                    if (editor == player) {
                        editProcess.getCurrentStep().onPlace(player, event.getBlock());
                        event.setCancelled(true);
                    }
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_START) {
                if (room.getRoomRule().isAllowDropItem()) {
                    RoomPlayerDropItemEvent roomPlayerDropItemEvent = new RoomPlayerDropItemEvent(room, player, event.getItem());
                    GameListenerRegistry.callEvent(room, roomPlayerDropItemEvent);
                    if (roomPlayerDropItemEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodeEvent(EntityExplodeEvent event) {
        List<Room> roomList = new ArrayList<>();
        RoomManager.getLoadedRooms().forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                for (AdvancedLocation location : room.getStartSpawn()) {
                    if (location.getLevel().getName().equals(event.getPosition().level.getName())) {
                        return;
                    }
                }
                if (!room.getRoomRule().isAllowExplosion()) {
                    event.getEntity().kill();
                    event.setCancelled(true);
                } else {
                    if (!room.getRoomRule().isAllowExplosionBreakBlock()) {
                        event.setBlockList(new ArrayList<>());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ExplodePrimeEvent(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.getLevel();
        List<Room> roomList = new ArrayList<>();
        RoomManager.getLoadedRooms().forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                if (room.getPlayLevels().contains(level)) {
                    if (!room.getRoomRule().isAllowExplosion()) {
                        entity.kill();
                        event.setCancelled(true);
                    }
                    RoomExplodePrimeEvent roomExplodePrimeEvent = new RoomExplodePrimeEvent(room, entity, event.getForce(), event.isBlockBreaking());
                    GameListenerRegistry.callEvent(room, roomExplodePrimeEvent);
                    if (!roomExplodePrimeEvent.isCancelled()) {
                        event.setForce(roomExplodePrimeEvent.getForce());
                        event.setBlockBreaking(roomExplodePrimeEvent.isBlockBreaking());
                    } else {
                        entity.kill();
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityExplosionPrimeEvent(EntityExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.getLevel();
        List<Room> roomList = new ArrayList<>();
        RoomManager.getLoadedRooms().forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                if (room.getPlayLevels().contains(level)) {
                    if (!room.getRoomRule().isAllowExplosion()) {
                        entity.kill();
                        event.setCancelled(true);
                    }
                    RoomEntityExplodePrimeEvent roomExplodeEvent = new RoomEntityExplodePrimeEvent(room, entity, event.getForce(), event.isBlockBreaking());
                    GameListenerRegistry.callEvent(room, roomExplodeEvent);
                    if (!roomExplodeEvent.isCancelled()) {
                        event.setForce(roomExplodeEvent.getForce());
                        event.setBlockBreaking(roomExplodeEvent.isBlockBreaking());
                    } else {
                        entity.kill();
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityExplodeEvent(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.getLevel();
        List<Room> roomList = new ArrayList<>();
        RoomManager.getLoadedRooms().forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                if (room.getPlayLevels().contains(level)) {
                    if (!room.getRoomRule().isAllowExplosion()) {
                        entity.kill();
                        event.setCancelled(true);
                    }
                    RoomEntityExplodeEvent roomExplodeEvent = new RoomEntityExplodeEvent(room, entity, event.getPosition(), event.getBlockList(), event.getYield());
                    GameListenerRegistry.callEvent(room, roomExplodeEvent);
                    if (!roomExplodeEvent.isCancelled()) {
                        event.setBlockList(roomExplodeEvent.getBlockList());
                        event.setYield(roomExplodeEvent.getYield());
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }
        Entity entity = event.getEntity();
        Room room = null;
        if (entity instanceof Player) {
            room = RoomManager.getRoom((Player) entity);
        } else {
            Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
            if (roomOptional.isPresent()) {
                room = roomOptional.get();
                RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage());
                GameListenerRegistry.callEvent(room, roomEntityDamageEvent);
                if (roomEntityDamageEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.setDamage(roomEntityDamageEvent.getDamage());
                }
                return;
            }
        }
        if (room == null) {
            return;
        }
        Player player = (Player) entity;
        if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) { // 未开始都不算数
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                BaseTeam team = room.getTeam(player);
                if (team != null) {
                    team.teleportToSpawn();
                } else {
                    player.teleport(player.getLevel().getSpawnLocation());
                }
            }
            event.setCancelled(true);
            return;
        }
        long diff = System.currentTimeMillis() - room.getPlayerProperty(player.getName(), "last_receive_entity_damage_millis", 0L);
        if (diff <= room.getRoomRule().getPlayerReceiveEntityDamageCoolDownMillis()) {
            event.setCancelled(true);
            return;
        }
        switch (event.getCause()) {
            case FALL:
                if (!room.getRoomRule().isAllowFallDamage()) {
                    event.setDamage(0f);
                    return;
                }
                break;
            case ENTITY_EXPLOSION:
                if (!room.getRoomRule().isAllowEntityExplosionDamage()) {
                    event.setDamage(0f);
                    return;
                }
                break;
            case BLOCK_EXPLOSION:
                if (!room.getRoomRule().isAllowBlockExplosionDamage()) {
                    event.setDamage(0f);
                    return;
                }
                break;
        }
        if (room.getRoomRule().isVirtualHealth()) {
            RoomVirtualHealthManager manager = room.getRoomVirtualHealthManager();
            if (manager.getHealth(player) - event.getFinalDamage() <= 0d) {
                manager.setHealth(player, manager.getMaxHealth());
                damageSources.remove(entity.getName());
                room.setDeath(player);
                if (room.getRoomRule().isAllowRespawn()) {
                    int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                    room.addRespawnTask(player, respawnTicks);
                    event.setCancelled(true);
                }
                return;
            }
        } else {
            if (entity.getHealth() - event.getFinalDamage() <= 0f) {
                room.setDeath(player); // 设置死亡
                if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
                    damageSources.remove(entity.getName());
                }
                if (room.getRoomRule().isAllowRespawn()) {
                    int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                    room.addRespawnTask(player, respawnTicks);
                }
                event.setCancelled(true);
                return;
            }
        }

        RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage());
        GameListenerRegistry.callEvent(room, roomEntityDamageEvent);
        if (roomEntityDamageEvent.isCancelled()) {
            event.setCancelled(true);
        } else {
            if (room.getRoomRule().isVirtualHealth()) {
                room.getRoomVirtualHealthManager().reduceHealth((Player) entity, BigDecimal.valueOf(roomEntityDamageEvent.getDamage()).doubleValue());
            } else {
                event.setDamage(roomEntityDamageEvent.getDamage());
            }
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity entity2 = event.getDamager();
        if (entity2 instanceof Player && entity instanceof Player) {
            Room room1 = RoomManager.getRoom((Player) entity);
            Room room2 = RoomManager.getRoom((Player) entity2);
            if (room1 != null && room1 == room2) {
                Player victim = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                if (room1.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                    event.setCancelled(true);
                    return;
                }
                if (!room1.getRoomRule().isAllowDamagePlayer()) {
                    event.setCancelled(true);
                    return;
                }
                if (!room1.getTeams().isEmpty()) {
                    if (room1.getTeam(victim) != null && room1.getTeam(victim) == room2.getTeam(damager)) {
                        victim.sendMessage(GameAPI.getLanguage().getTranslation(victim, "baseEvent.team_damage.not_allowed"));
                        event.setCancelled(true);
                        return;
                    }
                }
                if (room1.getRoomRule().isVirtualHealth()) {
                    RoomVirtualHealthManager manager = room1.getRoomVirtualHealthManager();
                    if (manager.getHealth(victim) - event.getFinalDamage() <= 0d) {
                        addPlayerDamageSource(victim.getName(), damager.getName());
                        room1.setDeath(victim); // 设置死亡
                        manager.setHealth(victim, manager.getMaxHealth());
                        damageSources.remove(entity.getName());
                        if (room1.getRoomRule().isAllowRespawn()) {
                            int respawnTicks = room1.getRoomRule().getRespawnCoolDownTick();
                            room1.addRespawnTask(victim, respawnTicks);
                        }
                        return;
                    }
                } else {
                    if (entity.getHealth() - event.getFinalDamage() <= 0f) {
                        addPlayerDamageSource(victim.getName(), damager.getName());
                        room1.setDeath(victim); // 设置死亡
                        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                            if (room1.getRoomRule().isAllowRespawn()) {
                                int respawnTicks = room1.getRoomRule().getRespawnCoolDownTick();
                                room1.addRespawnTask(victim, respawnTicks);
                            }
                        } else {
                            entity.setHealth(entity.getMaxHealth());
                            damageSources.remove(entity.getName());
                            if (room1.getRoomRule().isAllowRespawn()) {
                                int respawnTicks = room1.getRoomRule().getRespawnCoolDownTick();
                                room1.addRespawnTask(victim, respawnTicks);
                            }
                        }
                        return;
                    }
                }
                if (!room1.getRoomRule().isUseDefaultAttackCooldown()) {
                    long cd = room1.getRoomRule().getAttackCoolDownMillis();
                    if (cd > 0) {
                        long diff = System.currentTimeMillis() - room1.getPlayerProperty(damager, "last_attack_millis", 0L);
                        if (diff < cd) {
                            event.setCancelled(true);
                            return;
                        }
                        room1.setPlayerProperty(damager, "last_attack_millis", System.currentTimeMillis());
                    }
                }

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, entity, damager, event.getDamage(), event.getFinalDamage(), event.getAttackCooldown(), event.getKnockBack(), event.getCause());
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);
                GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    if (event.getEntity() instanceof Player && room1.getRoomRule().isVirtualHealth()) {
                        room1.getRoomVirtualHealthManager().reduceHealth((Player) event.getEntity(), BigDecimal.valueOf(roomEntityDamageByEntityEvent.getDamage()).doubleValue());
                        event.setDamage(0);
                    } else {
                        for (EntityDamageEvent.DamageModifier value : EntityDamageEvent.DamageModifier.values()) {
                            event.setDamage(roomEntityDamageByEntityEvent.getDamage(value), value);
                        }
                        event.setDamage(roomEntityDamageByEntityEvent.getDamage());
                    }
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                    addPlayerDamageSource(victim.getName(), damager.getName());
                }
            }
        } else {
            Optional<Room> room = RoomManager.getRoom(entity.getLevel());
            if (room.isPresent()) {
                if (room.get().getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                    event.setCancelled(true);
                    return;
                }

                Room room1 = room.get();
                if (entity instanceof Player) {
                    long diff = System.currentTimeMillis() - room1.getPlayerProperty(entity.getName(), "last_receive_entity_damage_millis", 0L);
                    if (diff <= room1.getRoomRule().getPlayerReceiveEntityDamageCoolDownMillis()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, event.getEntity(), event.getDamager(), event.getDamage(), event.getFinalDamage(), event.getAttackCooldown(), event.getKnockBack(), event.getCause());
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);
                GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                    event.setDamage(roomEntityDamageByEntityEvent.getDamage());
                    if (event.getDamager() instanceof Player) {
                        lastLivingEntityDamagedByPlayerSources.put(entity.getId(), new PlayerDamageSource((Player) event.getDamager(), System.currentTimeMillis()));
                    } else {
                        lastLivingEntityDamagedByEntitySources.put(entity.getId(), new EntityDamageSource(event.getDamager(), System.currentTimeMillis()));
                    }
                    room1.setPlayerProperty(entity.getName(), "last_receive_entity_damage_millis", System.currentTimeMillis());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/gameapi") || command.startsWith("/hub")) {
            return;
        }
        Player player = event.getPlayer();
        if (player != null && !player.isOp()) {
            Room room = RoomManager.getRoom(player);
            if (room != null) {
                for (String allowCommand : room.getRoomRule().getAllowCommands()) {
                    if (command.startsWith(allowCommand) || command.startsWith("/" + allowCommand)) {
                        return;
                    }
                }
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.command_execute.not_allowed"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void PlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Level fromLevel = event.getFrom().getLevel();
        Level toLevel = event.getTo().getLevel();
        if (fromLevel == null || toLevel == null) {
            return;
        }
        Room room = RoomManager.getRoom(player);
        if (!fromLevel.equals(toLevel)) {
            if (room != null) {
                Set<Level> arenas = new HashSet<>();
                if (room.getWaitSpawn() != null && room.getWaitSpawn().isValid()) {
                    arenas.add(room.getWaitSpawn().getLevel());
                }
                if (room.getEndSpawn() != null && room.getEndSpawn().isValid()) {
                    arenas.add(room.getEndSpawn().getLevel());
                }
                for (AdvancedLocation location : room.getStartSpawn()) {
                    if (location != null && location.isValid()) {
                        arenas.add(location.getLevel());
                    }
                }
                if (!arenas.contains(toLevel)) {
                    if (arenas.contains(fromLevel)) {
                        if (room.getRoomRule().isAllowQuitByTeleport()) {
                            room.removePlayer(player);
                        } else {
                            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.level_change.not_allowed"));
                            event.setCancelled(true);
                        }
                    }
                }
            } else {
                Optional<Room> roomOptional = RoomManager.getRoom(toLevel);
                roomOptional.ifPresent(value -> value.addPlayer(player));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void CraftItemEvent(CraftItemEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (!room.getRoomRule().isAllowCraft()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room == null) {
            // Player is not in game, so the server will send the message to the players who are not in game.
            event.setRecipients(event.getRecipients()
                    .stream()
                    .filter(p -> !p.isPlayer() || RoomManager.getRoom((Player) p) == null)
                    .collect(Collectors.toSet()));
            return;
        }

        RoomChatData roomChatData = new RoomChatData(player.getName(), event.getMessage());
        // Player is in game, so we trigger RoomPlayerChatEvent.
        RoomPlayerChatEvent chatEvent = new RoomPlayerChatEvent(room, player, roomChatData);
        GameListenerRegistry.callEvent(room, chatEvent);
        if (!chatEvent.isCancelled()) {
            String rawMsg = roomChatData.getRawMessage();
            if (rawMsg.startsWith("@")) {
                if (!room.getTeams().isEmpty()) {
                    BaseTeam team = room.getTeam(player);
                    if (team != null) {
                        rawMsg = roomChatData.getRawMessage().replaceFirst("@", "");
                        roomChatData.setMessage(rawMsg);
                        String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format_team", room.getRoomName(), roomChatData.getDefaultChatMsg());
                        team.sendMessageToAll(msg);
                        GameAPI.getGameDebugManager().info(msg);
                    } else {
                        String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                        room.sendMessageToAll(msg);
                        GameAPI.getGameDebugManager().info(msg);
                    }
                } else {
                    String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                    room.sendMessageToAll(msg);
                    GameAPI.getGameDebugManager().info(msg);
                }
            } else if (rawMsg.startsWith("!") && rawMsg.length() > 1) {
                rawMsg = roomChatData.getRawMessage().replaceFirst("!", "");
                roomChatData.setMessage(rawMsg);
                for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                    String msg = GameAPI.getLanguage().getTranslation(value, "baseEvent.chat.message_format.global", room.getRoomName(), roomChatData.getDefaultChatMsg());
                    value.sendMessage(msg);
                }
                GameAPI.getGameDebugManager().info(GameAPI.getLanguage().getTranslation("baseEvent.chat.message_format.global", room.getRoomName(), roomChatData.getDefaultChatMsg()));
            } else {
                String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                room.sendMessageToAll(msg);
                GameAPI.getGameDebugManager().info(msg);
            }
        }
        event.setCancelled(true);
    }

    // Extra listener for GameListeners
    @EventHandler
    public void PlayerJumpEvent(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerJumpEvent roomPlayerJumpEvent = new RoomPlayerJumpEvent(room, player);
            GameListenerRegistry.callEvent(room, roomPlayerJumpEvent);
        }
    }

    @EventHandler
    public void PlayerToggleGlideEvent(PlayerToggleGlideEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerToggleGlideEvent roomPlayerToggleGlideEvent = new RoomPlayerToggleGlideEvent(room, player, event.isGliding());
            GameListenerRegistry.callEvent(room, roomPlayerToggleGlideEvent);
            if (roomPlayerToggleGlideEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerToggleSneakEvent roomPlayerToggleSneakEvent = new RoomPlayerToggleSneakEvent(room, player, event.isSneaking());
            GameListenerRegistry.callEvent(room, roomPlayerToggleSneakEvent);
            if (roomPlayerToggleSneakEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerToggleSprintEvent roomPlayerToggleSprintEvent = new RoomPlayerToggleSprintEvent(room, player, event.isSprinting());
            GameListenerRegistry.callEvent(room, roomPlayerToggleSprintEvent);
            if (roomPlayerToggleSprintEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            Item item = player.getInventory().getItemInHand();
            RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
            if (roomItemBase != null) {
                long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                if (System.currentTimeMillis() >= nextUseMillis) {
                    roomItemBase.onInteract(room, player, item);
                }
            } else {
                RoomPlayerInteractEvent roomPlayerInteractEvent = new RoomPlayerInteractEvent(room, player, event.getBlock(), event.getTouchVector(), event.getFace(), event.getItem(), event.getAction());
                GameListenerRegistry.callEvent(room, roomPlayerInteractEvent);
                if (roomPlayerInteractEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (GameAPI.worldEditPlayers.contains(player)) {
                Item item = player.getInventory().getItemInHand();
                switch (item.getId()) {
                    case Item.GOLDEN_AXE:
                        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
                            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
                        }
                        WorldEditCommand.posSetLinkedHashMap.get(player).setPos1(player.getLocation());
                        player.sendMessage("Successfully set pos1 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                        event.setCancelled(true);
                        return;
                    case Item.GOLDEN_SHOVEL:
                        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
                            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
                        }
                        WorldEditCommand.posSetLinkedHashMap.get(player).setPos2(player.getLocation());
                        player.sendMessage("Successfully set pos2 to " + player.getX() + ":" + player.getY() + ":" + player.getZ());
                        event.setCancelled(true);
                }
            } else {
                for (EditProcess editProcess : GameAPI.editProcessList) {
                    Player editor = editProcess.getPlayer();
                    if (editor == player) {
                        Block block = event.getBlock();
                        if (block.getId() == BlockID.AIR) {
                            editProcess.getCurrentStep().onInteractAir(player);
                        } else {
                            editProcess.getCurrentStep().onInteract(player, block);
                        }
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerItemConsumeEvent roomPlayerItemConsumeEvent = new RoomPlayerItemConsumeEvent(room, player, event.getItem());
            GameListenerRegistry.callEvent(room, roomPlayerItemConsumeEvent);
            if (roomPlayerItemConsumeEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null && event.getItem() != null) {
            Item item = event.getItem();
            RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
            if (roomItemBase != null) {
                long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                if (System.currentTimeMillis() >= nextUseMillis) {
                    roomItemBase.onItemHeld(room, player, item);
                }
            } else {
                RoomPlayerItemHeldEvent roomPlayerItemHeldEvent = new RoomPlayerItemHeldEvent(room, player, event.getItem());
                GameListenerRegistry.callEvent(room, roomPlayerItemHeldEvent);
                if (roomPlayerItemHeldEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getEntity() instanceof GameProjectileEntity) {
            Room room = ((GameProjectileEntity) event.getEntity()).getRoom();
            if (room == null) {
                return;
            }
            RoomProjectileHitEvent roomProjectileHitEvent = new RoomProjectileHitEvent(room, event.getEntity(), event.getMovingObjectPosition());
            GameListenerRegistry.callEvent(room, roomProjectileHitEvent);
            if (roomProjectileHitEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (((EntityProjectile) event.getEntity()).shootingEntity instanceof Player) {
            Room room = RoomManager.getRoom((Player) ((EntityProjectile) event.getEntity()).shootingEntity);
            if (room == null) {
                return;
            }
            RoomProjectileHitEvent roomProjectileHitEvent = new RoomProjectileHitEvent(room, event.getEntity(), event.getMovingObjectPosition());
            GameListenerRegistry.callEvent(room, roomProjectileHitEvent);
            if (roomProjectileHitEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof GameProjectileEntity) {
            Room room = ((GameProjectileEntity) event.getEntity()).getRoom();
            if (room == null) {
                return;
            }
            RoomProjectileLaunchEvent roomProjectileLaunchEvent = new RoomProjectileLaunchEvent(room, event.getEntity());
            GameListenerRegistry.callEvent(room, roomProjectileLaunchEvent);
            if (roomProjectileLaunchEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (event.getEntity().shootingEntity instanceof Player) {
            Room room = RoomManager.getRoom((Player) event.getEntity().shootingEntity);
            if (room == null) {
                return;
            }
            RoomProjectileLaunchEvent roomProjectileLaunchEvent = new RoomProjectileLaunchEvent(room, event.getEntity());
            GameListenerRegistry.callEvent(room, roomProjectileLaunchEvent);
            if (roomProjectileLaunchEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntitySpawnEvent(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
        if (!roomOptional.isPresent()) {
            return;
        }
        Room room = roomOptional.get();
        RoomEntitySpawnEvent roomEntitySpawnEvent = new RoomEntitySpawnEvent(room, entity);
        GameListenerRegistry.callEvent(room, roomEntitySpawnEvent);
    }

    @EventHandler
    public void EntityRegainHealthEvent(EntityRegainHealthEvent event) {
        Room room;
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            room = RoomManager.getRoom((Player) entity);
            if (room == null) {
                return;
            }
        } else {
            Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
            if (!roomOptional.isPresent()) {
                return;
            }
            room = roomOptional.get();
        }
        RoomEntityRegainHealthEvent roomEntityRegainHealthEvent = new RoomEntityRegainHealthEvent(room, entity, event.getAmount(), event.getRegainReason());
        GameListenerRegistry.callEvent(room, roomEntityRegainHealthEvent);
        if (roomEntityRegainHealthEvent.isCancelled()) {
            event.setCancelled(true);
        } else {
            if (room.getRoomRule().isVirtualHealth()) {
                if (entity instanceof Player) {
                    room.getRoomVirtualHealthManager().addHealth((Player) entity, BigDecimal.valueOf(roomEntityRegainHealthEvent.getAmount()).doubleValue());
                    event.setAmount(0);
                    return;
                }
            }
            event.setAmount(roomEntityRegainHealthEvent.getAmount());
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (room.getRoomRule().isVirtualHealth()) {
                if (room.getRoomVirtualHealthManager().getHealth(player) > 0) {
                    player.setHealth(player.getMaxHealth());
                    event.setCancelled(true);
                    return;
                }
            }
            room.setDeath(player);
            if (room.getRoomRule().isAllowRespawn()) {
                int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                room.addRespawnTask(player, respawnTicks);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving) {
            Room room;
            Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
            if (!roomOptional.isPresent()) {
                return;
            }
            room = roomOptional.get();
            RoomEntityDeathEvent entityDeathEvent = new RoomEntityDeathEvent(room, (EntityLiving) entity, event.getDrops());
            GameListenerRegistry.callEvent(room, entityDeathEvent);
            if (entityDeathEvent.isCancelled()) {
                event.setCancelled(true);
            } else {
                event.setDrops(entityDeathEvent.getDrops());
            }
        }
    }

    @EventHandler
    public void InventoryPickupItemEvent(InventoryPickupItemEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player) {
            Player player = (Player) holder;
            Room room = RoomManager.getRoom(player);
            if (room != null) {
                RoomInventoryPickupItemEvent roomInventoryPickupItemEvent = new RoomInventoryPickupItemEvent(room, event.getInventory(), event.getItem());
                GameListenerRegistry.callEvent(room, roomInventoryPickupItemEvent);
                if (roomInventoryPickupItemEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        event.getPlayer().setCheckMovement(false);
    }

    @EventHandler
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void DataPacketReceiveEvent(DataPacketReceiveEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null && room.getPlayers().contains(player)) {
            if ((room.getRoomStatus() == RoomStatus.ROOM_STATUS_READY_START
                    || room.getRoomStatus() == RoomStatus.ROOM_STATUS_NEXT_ROUND_PRESTART)
                    && !room.getRoomRule().isAllowReadyStartWalk()) {
                if (event.getPacket() instanceof MovePlayerPacket) {
                    MovePlayerPacket pk = (MovePlayerPacket) event.getPacket();
                    if (new Vector3(pk.x, player.y, pk.z).distance(player) == 0d) {
                        return;
                    }
                    Location location = player.getLocation();
                    location.setYaw(pk.yaw);
                    location.setPitch(pk.pitch);
                    location.setHeadYaw(pk.headYaw);
                    player.teleport(location);
                    event.setCancelled(true);
                }
            }
        }
    }
}