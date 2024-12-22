package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.activity.ActivityLobbyTask;
import gameapi.commands.worldedit.WorldEditCommand;
import gameapi.entity.GameProjectileEntity;
import gameapi.event.block.RoomBlockBreakEvent;
import gameapi.event.block.RoomBlockIgniteEvent;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.entity.*;
import gameapi.event.inventory.RoomInventoryPickupArrowEvent;
import gameapi.event.inventory.RoomInventoryPickupItemEvent;
import gameapi.event.player.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.data.GlobalSettingsManager;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.room.*;
import gameapi.room.edit.EditProcess;
import gameapi.room.items.RoomItemBase;
import gameapi.room.team.BaseTeam;
import gameapi.room.utils.QuitRoomReason;
import gameapi.tools.FireworkTools;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.PosSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
public class BaseEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GlobalSettingsManager.removeCacheAndSaveData(player);
        GameAPI.getGameDebugManager().removePlayer(player);
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            room.sendMessageToAll(GameAPI.getLanguage().getTranslation("baseEvent.quit.success", player.getName()));
            room.removePlayer(player);
            room.removeSpectator(player);
            player.setPosition(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
        }
        for (EditProcess editProcess : GameAPI.editProcessList) {
            Player editor = editProcess.getPlayer();
            if (editor == player) {
                editProcess.onQuit();
            }
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        Block block = event.getBlock();
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                event.setCancelled(true);
            }
            if (room.getRoomRule().isProtectMapBlock() && room.getBlocks().stream().noneMatch(blockVector3 -> blockVector3.equals(block.asBlockVector3()))) {
                if (!room.getRoomRule().getAllowBreakProtectedMapBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage())
                        && !room.getRoomRule().getAllowBreakProtectedMapBlocks().contains(event.getBlock().getId() + "")) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (!room.getRoomRule().isAllowBreakBlock()) {
                if (!room.getRoomRule().getAllowBreakBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage()) && !room.getRoomRule().getAllowBreakBlocks().contains(event.getBlock().getId() + "")) {
                    event.setCancelled(true);
                } else {
                    RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, block, player, event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                    GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                    if (roomBlockBreakEvent.isCancelled()) {
                        event.setCancelled(true);
                    } else {
                        room.getBlocks().removeIf(blockVector3 -> blockVector3.equals(block.asBlockVector3()));
                        event.setDropExp(roomBlockBreakEvent.getDropExp());
                        event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                        event.setDrops(roomBlockBreakEvent.getDrops());
                    }
                }
            } else {
                RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, block, player, event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                if (roomBlockBreakEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    room.getBlocks().removeIf(blockVector3 -> blockVector3.equals(block.asBlockVector3()));
                    event.setDropExp(roomBlockBreakEvent.getDropExp());
                    event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                    event.setDrops(roomBlockBreakEvent.getDrops());
                }
            }
        } else {
            if (GameAPI.worldEditPlayers.contains(player)) {
                if (block != null) {
                    if (block.getId() != 0) {
                        player.sendMessage("方块: " + block.toItem().getNamespaceId() + ", 位置: " + block.getFloorX() + ":" + block.getFloorY() + ":" + block.getFloorZ());
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
                    if (room.getRoomRule().isProtectMapBlock()) {
                        room.getBlocks().add(event.getBlock().asBlockVector3());
                    }
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
                    } else {
                        if (!room.getRoomRule().isAllowExplosionBreakBlock()) {
                            event.setBlockList(new ArrayList<>());
                        }
                    }
                    RoomEntityExplodeEvent roomExplodeEvent = new RoomEntityExplodeEvent(room, entity, event.getPosition(), event.getBlockList(), event.getYield());
                    GameListenerRegistry.callEvent(room, roomExplodeEvent);
                    if (!roomExplodeEvent.isCancelled()) {
                        roomExplodeEvent.getBlockList().removeIf(block -> room.getBlocks().stream().noneMatch(b -> b.equals(block.asBlockVector3())));
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
    public void BlockIgniteEvent(BlockIgniteEvent event) {
        Block block = event.getBlock();
        for (Room room : RoomManager.getRooms(block.getLevel())) {
            RoomBlockIgniteEvent roomEvent = new RoomBlockIgniteEvent(room, event.getBlock(), event.getSource(), event.getEntity(), event.getCause());
            GameListenerRegistry.callEvent(room, roomEvent);
            if (roomEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }
        Entity entity = event.getEntity();
        List<Room> rooms = new ArrayList<>();
        if (entity instanceof Player) {
            Room room = RoomManager.getRoom((Player) entity);
            if (room != null) {
                rooms.add(room);
            }
        } else {
            rooms = RoomManager.getRooms(entity.getLevel());
            for (Room room : rooms) {
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
        for (Room room : rooms) {
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
            long diff = System.currentTimeMillis() - room.getPlayerProperty(player.getName(), DefaultPropertyKey.KEY_LAST_RECEIVE_ENTITY_DAMAGE_MILLIS, 0L);
            if (diff <= room.getRoomRule().getPlayerReceiveEntityDamageCoolDownMillis()) {
                event.setCancelled(true);
                return;
            }
            switch (event.getCause()) {
                case FALL:
                    if (!room.getRoomRule().isAllowFallDamage()) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case ENTITY_EXPLOSION:
                    if (!room.getRoomRule().isAllowEntityExplosionDamage()) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case BLOCK_EXPLOSION:
                    if (!room.getRoomRule().isAllowBlockExplosionDamage()) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
            }
            if (!room.getRoomRule().isVirtualHealth()) {
                if (entity.getHealth() - event.getFinalDamage() <= 0f) {
                    room.setDeath(player); // 设置死亡
                    event.setCancelled(true);
                    return;
                }
            }

            RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage());

            Item item = player.getInventory().getItemInHand();
            RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
            if (roomItemBase != null) {
                long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                if (System.currentTimeMillis() >= nextUseMillis) {
                    roomItemBase.onHurt(item, roomEntityDamageEvent);
                }
            }

            if (!roomEntityDamageEvent.isCancelled()) {
                GameListenerRegistry.callEvent(room, roomEntityDamageEvent);
            }

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
    }

    @EventHandler(priority = EventPriority.MONITOR)
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
                        victim.sendMessage(GameAPI.getLanguage().getTranslation(damager, "baseEvent.team_damage.not_allowed"));
                        event.setCancelled(true);
                        return;
                    }
                }
                if (!room1.getRoomRule().isUseDefaultAttackCooldown()) {
                    long cd = room1.getRoomRule().getAttackCoolDownMillis();
                    if (cd > 0) {
                        long diff = System.currentTimeMillis() - room1.getPlayerProperty(damager, DefaultPropertyKey.KEY_LAST_ATTACK_MILLIS, 0L);
                        if (diff < cd) {
                            event.setCancelled(true);
                            return;
                        }
                        room1.setPlayerProperty(damager, DefaultPropertyKey.KEY_LAST_ATTACK_MILLIS, System.currentTimeMillis());
                    }
                }

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, entity, damager, event.getAttackCooldown(), event.getKnockBack(), event.getCause());
                BasicAttackSetting basicAttackSetting = room1.getRoomRule().getBasicAttackSetting();
                if (basicAttackSetting != null) {
                    roomEntityDamageByEntityEvent.setKnockBack(basicAttackSetting.getBaseKnockBack());
                    roomEntityDamageByEntityEvent.setAttackCoolDown(basicAttackSetting.getAttackCoolDown());
                }
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);

                Item item = damager.getInventory().getItemInHand();
                RoomItemBase roomItemBase = room2.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                if (roomItemBase != null) {
                    long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                    if (System.currentTimeMillis() >= nextUseMillis) {
                        roomItemBase.onEntityDamageByEntity(item, event);
                    }
                }

                if (!roomEntityDamageByEntityEvent.isCancelled()) {
                    GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
                }

                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    if (event.getEntity() instanceof Player && room1.getRoomRule().isVirtualHealth()) {
                        if (entity.getHealth() - event.getFinalDamage() <= 0f) {
                            room1.setDeath(victim); // 设置死亡
                        } else if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                            room1.setDeath(victim); // 设置死亡
                        } else {
                            room1.getRoomVirtualHealthManager().reduceHealth((Player) event.getEntity(), BigDecimal.valueOf(roomEntityDamageByEntityEvent.getFinalDamage()).doubleValue());
                            event.setDamage(0);
                        }
                    } else {
                        for (EntityDamageEvent.DamageModifier value : EntityDamageEvent.DamageModifier.values()) {
                            event.setDamage(roomEntityDamageByEntityEvent.getDamage(value), value);
                        }
                    }
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                    room1.addEntityDamageSource(victim, damager, event.getFinalDamage());
                }
            }
        } else {
            for (Room room : RoomManager.getRooms(entity.getLevel())) {
                if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                    event.setCancelled(true);
                    return;
                }
                if (entity instanceof Player) {
                    Room room1 = RoomManager.getRoom((Player) entity);
                    if (!room1.getRoomRule().isAllowEnderPearlDamage()) {
                        if (event.getDamager() instanceof EntityEnderPearl) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    long diff = System.currentTimeMillis() - room.getPlayerProperty(entity.getName(), DefaultPropertyKey.KEY_LAST_RECEIVE_ENTITY_DAMAGE_MILLIS, 0L);
                    if (diff <= room.getRoomRule().getPlayerReceiveEntityDamageCoolDownMillis()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room, event.getEntity(), event.getDamager(), event.getAttackCooldown(), event.getKnockBack(), event.getCause());
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);
                GameListenerRegistry.callEvent(room, roomEntityDamageByEntityEvent);
                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                    event.setDamage(roomEntityDamageByEntityEvent.getDamage());
                    if (event.getDamager() instanceof Player) {
                        Player damager = (Player) event.getDamager();
                        Item item = damager.getInventory().getItemInHand();
                        RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                        if (roomItemBase != null) {
                            long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                            if (System.currentTimeMillis() >= nextUseMillis) {
                                roomItemBase.onEntityDamageByEntity(item, event);
                            }
                        }
                    }
                    if (event.getEntity() instanceof Player && room.getRoomRule().isVirtualHealth()) {
                        room.getRoomVirtualHealthManager().reduceHealth((Player) event.getEntity(), BigDecimal.valueOf(roomEntityDamageByEntityEvent.getFinalDamage()).doubleValue());
                        event.setDamage(0);
                    }
                    room.addEntityDamageSource(entity, event.getDamager(), event.getFinalDamage());
                    room.setPlayerProperty(entity.getName(), DefaultPropertyKey.KEY_LAST_RECEIVE_ENTITY_DAMAGE_MILLIS, System.currentTimeMillis());
                }
            }
        }
    }

    @EventHandler
    public void onMotion(EntityMotionEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.getLevel();
        for (Room room : RoomManager.getRooms(level)) {
            BasicAttackSetting basicAttackSetting = room.getRoomRule().getBasicAttackSetting();
            if (basicAttackSetting != null) {
                Vector3 v = event.getMotion();
                v.x *= basicAttackSetting.getMotionXZ();
                v = event.getMotion();
                if (entity.isOnGround()) {
                    v.y *= basicAttackSetting.getMotionY();
                } else {
                    v.y *= basicAttackSetting.getAirMotionY();
                }

                v = event.getMotion();
                v.z *= basicAttackSetting.getMotionXZ();
            }

            RoomEntityMotionEvent rev = new RoomEntityMotionEvent(room, entity, event.getMotion());
            GameListenerRegistry.callEvent(room, rev);
            if (rev.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ItemSpawnEvent(ItemSpawnEvent event) {
        EntityItem entity = event.getEntity();
        for (Room room : RoomManager.getRooms(entity.getLevel())) {
            RoomItemSpawnEvent rev = new RoomItemSpawnEvent(room, entity);
            GameListenerRegistry.callEvent(room, rev);
            if (rev.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ItemDespawnEvent(ItemDespawnEvent event) {
        EntityItem entity = event.getEntity();
        for (Room room : RoomManager.getRooms(entity.getLevel())) {
            RoomItemDespawnEvent rev = new RoomItemDespawnEvent(room, entity);
            GameListenerRegistry.callEvent(room, rev);
            if (rev.isCancelled()) {
                event.setCancelled(true);
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
                arenas.addAll(room.getPlayLevels());
                if (!arenas.contains(toLevel)) {
                    if (arenas.contains(fromLevel)) {
                        if (room.getRoomRule().isAllowQuitByTeleport()) {
                            List<Room> rooms = RoomManager.getRooms(toLevel);
                            Room targetRoom = null;
                            if (!rooms.isEmpty()) {
                                targetRoom = rooms.get(0);
                            }
                            if (targetRoom != null) {
                                if (targetRoom != room) {
                                    room.removePlayer(player, QuitRoomReason.TELEPORT);
                                    room.removeSpectator(player);
                                    targetRoom.addPlayer(player);
                                }
                            } else {
                                room.removePlayer(player, QuitRoomReason.TELEPORT);
                                room.removeSpectator(player);
                            }
                        } else {
                            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.level_change.not_allowed"));
                            event.setCancelled(true);
                        }
                    }
                }
            } else {
                List<Room> rooms = RoomManager.getRooms(toLevel);
                if (!rooms.isEmpty()) {
                    Room room1 = rooms.get(0);
                    room1.addPlayer(player);
                }
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
            event.setCancelled(true);
            String rawMsg = roomChatData.getRawMessage();
            if (rawMsg.startsWith("@")) {
                if (!room.getTeams().isEmpty()) {
                    BaseTeam team = room.getTeam(player);
                    if (team != null) {
                        rawMsg = roomChatData.getRawMessage().replaceFirst("@", "");
                        roomChatData.setMessage(rawMsg);
                        String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format_team", room.getRoomName(), roomChatData.getDefaultChatMsg());
                        team.sendMessageToAll(msg);
                        GameAPI.getGameDebugManager().info(msg, false);
                    } else {
                        String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                        room.sendMessageToAll(msg);
                        GameAPI.getGameDebugManager().info(msg, false);
                    }
                } else {
                    String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                    room.sendMessageToAll(msg);
                    GameAPI.getGameDebugManager().info(msg, false);
                }
            } else if (rawMsg.startsWith("!") && rawMsg.length() > 1) {
                rawMsg = roomChatData.getRawMessage().replaceFirst("!", "");
                roomChatData.setMessage(rawMsg);
                for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                    String msg = GameAPI.getLanguage().getTranslation(value, "baseEvent.chat.message_format.global", room.getRoomName(), roomChatData.getDefaultChatMsg());
                    value.sendMessage(msg);
                }
                GameAPI.getGameDebugManager().info(GameAPI.getLanguage().getTranslation("baseEvent.chat.message_format.global", room.getRoomName(), roomChatData.getDefaultChatMsg()), false);
            } else {
                String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), roomChatData.getDefaultChatMsg());
                room.sendMessageToAll(msg);
                GameAPI.getGameDebugManager().info(msg, false);
            }
        }
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
    public void PlayerToggleCrawlEvent(PlayerToggleCrawlEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerToggleCrawlEvent roomPlayerToggleCrawlEvent = new RoomPlayerToggleCrawlEvent(room, player, event.isCrawling());
            GameListenerRegistry.callEvent(room, roomPlayerToggleCrawlEvent);
            if (roomPlayerToggleCrawlEvent.isCancelled()) {
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
            if (event.getAction() != PlayerInteractEvent.Action.PHYSICAL) {
                RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                if (roomItemBase != null) {
                    long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                    if (System.currentTimeMillis() >= nextUseMillis) {
                        roomItemBase.onInteract(room, player, item, event.getAction());
                        return;
                    }
                }
            }
            RoomPlayerInteractEvent roomPlayerInteractEvent = new RoomPlayerInteractEvent(room, player, event.getBlock(), event.getTouchVector(), event.getFace(), event.getItem(), event.getAction());
            GameListenerRegistry.callEvent(room, roomPlayerInteractEvent);
            if (roomPlayerInteractEvent.isCancelled()) {
                event.setCancelled(true);
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
                }
            }
            /*
            if (GameAPI.getInstance().isGlorydarkRelatedFeature()) {
                if (player.getLevelName().equals("world") && event.getBlock() != null && event.getBlock().getId() == Block.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    if (event.getBlock().distance(new Vector3(84, 45, -42)) <= 1) {
                        boolean parkourFinishStatus = PlayerGameDataManager.getPlayerGameData(ActivityLobbyTask.activityId, "parkour_finished", player.getName(), false);
                        if (!parkourFinishStatus) {
                            FireworkTools.spawnRandomFirework(player);
                            player.sendMessage(TextFormat.GREEN + "恭喜你完成了主城跑酷，请前往福利姬的主城活动界面领取奖励吧！");
                            PlayerGameDataManager.setPlayerGameData(ActivityLobbyTask.activityId, "parkour_finished", player.getName(), true);
                        }
                    } else if (event.getBlock().distance(new Vector3(65, 33, -37)) <= 1) {
                        if (!QuestAPI.hasPlayerDailyProcess(player.getName(), "lobby_2412_christmas_parkour")) {
                            FireworkTools.spawnRandomFirework(player);
                            player.sendMessage(TextFormat.GREEN + "恭喜你完成了每日圣诞跑酷，请前往任务系统领取奖励哦！");
                            QuestAPI.addPlayerDailyProcess(player.getName(), "lobby_2412_christmas_parkour", 1);
                            QuestAPI.addPlayerPermanentProcess(player.getName(), "lobby_2412_christmas_parkour_accumulation", 1);
                        }
                    }
                }
            }
             */
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
        for (Room room : RoomManager.getRooms(entity.getLevel())) {
            RoomEntitySpawnEvent roomEntitySpawnEvent = new RoomEntitySpawnEvent(room, entity);
            GameListenerRegistry.callEvent(room, roomEntitySpawnEvent);
        }
    }

    @EventHandler
    public void EntityRegainHealthEvent(EntityRegainHealthEvent event) {
        List<Room> rooms = new ArrayList<>();
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Room room = RoomManager.getRoom((Player) entity);
            if (room != null) {
                rooms.add(room);
            }
        } else {
            rooms = RoomManager.getRooms(entity.getLevel());
        }
        for (Room room : rooms) {
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
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (room.getRoomRule().isVirtualHealth()) {
                if (room.getRoomVirtualHealthManager().getHealth(player) > 0d) {
                    player.setHealth(1);
                    event.setCancelled(true);
                    return;
                }
            }
            room.setDeath(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving) {
            for (Room room : RoomManager.getRooms(entity.getLevel())) {
                RoomEntityDeathEvent entityDeathEvent = new RoomEntityDeathEvent(room, (EntityLiving) entity, event.getDrops());
                GameListenerRegistry.callEvent(room, entityDeathEvent);
                if (entityDeathEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.setDrops(entityDeathEvent.getDrops());
                }
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
    public void InventoryPickupArrowEvent(InventoryPickupArrowEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player) {
            Player player = (Player) holder;
            Room room = RoomManager.getRoom(player);
            if (room != null) {
                RoomInventoryPickupArrowEvent rev = new RoomInventoryPickupArrowEvent(room, event.getInventory(), event.getArrow());
                GameListenerRegistry.callEvent(room, rev);
                if (rev.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void EntityEffectUpdateEvent(EntityEffectUpdateEvent event) {
        for (Room room : RoomManager.getRooms(event.getEntity().getLevel())) {
            RoomEntityEffectUpdateEvent roomEntityEffectUpdateEvent = new RoomEntityEffectUpdateEvent(room, event.getEntity(), event.getOldEffect(), event.getNewEffect());
            GameListenerRegistry.callEvent(room, roomEntityEffectUpdateEvent);
            if (roomEntityEffectUpdateEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntityEffectRemoveEvent(EntityEffectRemoveEvent event) {
        for (Room room : RoomManager.getRooms(event.getEntity().getLevel())) {
            RoomEntityEffectRemoveEvent roomEntityEffectRemoveEvent = new RoomEntityEffectRemoveEvent(room, event.getEntity(), event.getRemoveEffect());
            GameListenerRegistry.callEvent(room, roomEntityEffectRemoveEvent);
            if (roomEntityEffectRemoveEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            RoomPlayerBedEnterEvent bedEnterEvent = new RoomPlayerBedEnterEvent(room, player, event.getBed());
            GameListenerRegistry.callEvent(room, bedEnterEvent);
            if (bedEnterEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GlobalSettingsManager.loadPlayerData(player);
        player.setCheckMovement(false);
    }

    @EventHandler
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        event.setCancelled(true);
    }

    /*
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
     */
}