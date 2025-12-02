package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockRedstoneLamp;
import cn.nukkit.block.BlockTrapdoor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import gameapi.GameAPI;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.defaults.dev.HideChatCommand;
import gameapi.entity.GameProjectileEntity;
import gameapi.event.block.*;
import gameapi.event.entity.*;
import gameapi.event.extra.EntityDamageByEntityByItemEvent;
import gameapi.event.inventory.RoomInventoryPickupArrowEvent;
import gameapi.event.inventory.RoomInventoryPickupItemEvent;
import gameapi.event.player.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.data.GlobalSettingsManager;
import gameapi.manager.tools.PlayerTempStateManager;
import gameapi.room.Room;
import gameapi.room.RoomChatData;
import gameapi.room.edit.EditProcess;
import gameapi.room.items.RoomItemBase;
import gameapi.room.team.BaseTeam;
import gameapi.room.utils.BasicAttackSetting;
import gameapi.room.utils.DefaultPropertyKey;
import gameapi.room.utils.reason.JoinRoomReason;
import gameapi.room.utils.reason.QuitRoomReason;
import gameapi.tools.DecimalTools;
import gameapi.tools.EntityTools;
import gameapi.tools.PlayerTools;
import gameapi.utils.PosSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 */
public class BaseEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GlobalSettingsManager.removeCacheAndSaveData(player);
        GameAPI.getGameDebugManager().removePlayer(player);
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            room.sendMessageToAll(GameAPI.getLanguage().getTranslation("baseEvent.quit.success", player.getName()));
            room.removePlayer(player, QuitRoomReason.PLAYER_OFFLINE);
            room.removeSpectator(player);
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
        }
        for (EditProcess editProcess : GameAPI.editProcessList) {
            Player editor = editProcess.getPlayer();
            if (editor == player) {
                editProcess.onQuit();
            }
            break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void BlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        Block block = event.getBlock();
        if (room != null) {
            if (!room.getCurrentRoomStatus().isAllowBreakBlock(room)) {
                event.setCancelled(true);
                return;
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
                        GameAPI.getGameDebugManager().info("方块: " + block.toItem().getNamespaceId() + ", 位置: " + block.getFloorX() + ":" + block.getFloorY() + ":" + block.getFloorZ());
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            Item item = player.getInventory().getItemInHand();
            if (item != null) {
                RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                if (roomItemBase != null) {
                    long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                    if (System.currentTimeMillis() >= nextUseMillis) {
                        roomItemBase.onBlockPlace(room, player, item);
                        roomItemBase.onBlockPlace(room, player, item, event.getBlock());
                    }
                    if (roomItemBase.isCancelBlockPlaceEvent()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if (room.getCurrentRoomStatus().isAllowPlaceBlock(room)) {
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
            } else {
                event.setCancelled(true);
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
                        player.sendMessage("Successfully set pos1 to " + block.getX() + ":" + block.getY() + ":" + block.getZ());
                        event.setCancelled(true);
                        break;
                    case Block.EMERALD_BLOCK:
                        if (!WorldEditCommand.posSetLinkedHashMap.containsKey(player)) {
                            WorldEditCommand.posSetLinkedHashMap.put(player, new PosSet());
                        }
                        WorldEditCommand.posSetLinkedHashMap.get(player).setPos2(block.getLocation());
                        player.sendMessage("Successfully set pos2 to " + block.getX() + ":" + block.getY() + ":" + block.getZ());
                        event.setCancelled(true);
                        break;
                }
            } else {
                for (EditProcess editProcess : GameAPI.editProcessList) {
                    Player editor = editProcess.getPlayer();
                    if (editor == player) {
                        editProcess.getCurrentStep().onPlace(player, event.getBlock());
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (room.getCurrentRoomStatus().isAllowPlayerDropItem(room) && room.getRoomRule().isAllowDropItem()) {
                RoomPlayerDropItemEvent roomPlayerDropItemEvent = new RoomPlayerDropItemEvent(room, player, event.getItem());
                GameListenerRegistry.callEvent(room, roomPlayerDropItemEvent);
                if (roomPlayerDropItemEvent.isCancelled()) {
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
                RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage(), event);
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
            if (!room.getCurrentRoomStatus().isAllowEntityDamageBySelf(room)) {
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

            RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage(), event);

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
        Item damageItem;
        if (event instanceof EntityDamageByChildEntityEvent ev) {
            damageItem = EntityTools.getEntityProjectileItem(ev.getChild());
        } else if (event instanceof EntityDamageByEntityByItemEvent ev) {
            damageItem = ev.getItem();
        } else {
            damageItem = EntityTools.getEntityItemInHand(entity2);
        }
        if (entity2 instanceof Player && entity instanceof Player) {
            Room room1 = RoomManager.getRoom((Player) entity);
            Room room2 = RoomManager.getRoom((Player) entity2);
            if (room1 != null && room1 == room2) {
                if (!room1.getCurrentRoomStatus().isAllowEntityDamagedByEntity(room1)) {
                    event.setCancelled(true);
                    return;
                }
                Player victim = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                if (!room1.getRoomRule().isAllowDamagePlayer() && event.getEntity().isPlayer) {
                    event.setCancelled(true);
                    return;
                }
                if (!room1.getTeams().isEmpty()) {
                    if (victim != damager) {
                        if (room1.getTeam(victim) != null && room1.getTeam(victim) == room2.getTeam(damager)) {
                            damager.sendMessage(GameAPI.getLanguage().getTranslation(damager, "baseEvent.team_damage.not_allowed"));
                            event.setCancelled(true);
                            return;
                        }
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

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, entity, damager, damageItem, event.getAttackCooldown(), event.getKnockBack(), event.getCause(), event);
                BasicAttackSetting basicAttackSetting = room1.getRoomRule().getBasicAttackSetting();
                if (basicAttackSetting != null) {
                    roomEntityDamageByEntityEvent.setKnockBack(basicAttackSetting.getBaseKnockBack());
                    roomEntityDamageByEntityEvent.setAttackCoolDown(basicAttackSetting.getAttackCoolDown());
                }
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);

                RoomItemBase roomItemBase = room2.getRoomItem(RoomItemBase.getRoomItemIdentifier(damageItem));
                if (roomItemBase != null) {
                    long nextUseMillis = damageItem.getNamedTag().getLong("next_use_millis");
                    if (System.currentTimeMillis() >= nextUseMillis) {
                        roomItemBase.onEntityDamageByEntity(damageItem, event);
                    }
                }
                GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);

                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    room1.addEntityDamageSource(victim, damager, damageItem, roomEntityDamageByEntityEvent.getFinalDamage(), event);
                    if (event.getEntity() instanceof Player entityPlayer && room1.getRoomRule().isVirtualHealth()) {
                        if (room1.getRoomVirtualHealthManager().getHealth(entityPlayer) - roomEntityDamageByEntityEvent.getFinalDamage() <= 0f) {
                            room1.setDeath(victim); // 设置死亡
                        } else if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                            room1.setDeath(victim); // 设置死亡
                        } else {
                            room1.getRoomVirtualHealthManager().reduceHealth((Player) roomEntityDamageByEntityEvent.getEntity(), BigDecimal.valueOf(roomEntityDamageByEntityEvent.getFinalDamage()).doubleValue());
                        }
                        event.setDamage(0f);
                    } else {
                        for (EntityDamageEvent.DamageModifier value : EntityDamageEvent.DamageModifier.values()) {
                            event.setDamage(roomEntityDamageByEntityEvent.getDamage(value), value);
                        }
                    }
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                }
            }
        } else {
            for (Room room : RoomManager.getRooms(entity.getLevel())) {
                if (!room.getCurrentRoomStatus().isAllowEntityDamagedByEntity(room)) {
                    event.setCancelled(true);
                    return;
                }
                if (entity instanceof Player) {
                    if (!room.getRoomRule().isAllowDamagePlayer()) {
                        event.setCancelled(true);
                        return;
                    }
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

                RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room, event.getEntity(), event.getDamager(), damageItem, event.getAttackCooldown(), event.getKnockBack(), event.getCause(), event);
                roomEntityDamageByEntityEvent.parseDamageModifierFloatMap(event);
                GameListenerRegistry.callEvent(room, roomEntityDamageByEntityEvent);

                if (roomEntityDamageByEntityEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    room.addEntityDamageSource(entity, event.getDamager(), damageItem, roomEntityDamageByEntityEvent.getFinalDamage(), event);
                    room.setPlayerProperty(entity.getName(), DefaultPropertyKey.KEY_LAST_RECEIVE_ENTITY_DAMAGE_MILLIS, System.currentTimeMillis());
                    if (event.getDamager() instanceof Player damager) {
                        Item item = damager.getInventory().getItemInHand();
                        RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
                        if (roomItemBase != null) {
                            long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                            if (System.currentTimeMillis() >= nextUseMillis) {
                                roomItemBase.onEntityDamageByEntity(item, event);
                            }
                        }
                    }
                    if (event.getEntity() instanceof Player player && room.getRoomRule().isVirtualHealth()) {
                        room.getRoomVirtualHealthManager().reduceHealth(player, BigDecimal.valueOf(roomEntityDamageByEntityEvent.getFinalDamage()).doubleValue());
                        event.setDamage(0);
                    } else {
                        for (EntityDamageEvent.DamageModifier value : EntityDamageEvent.DamageModifier.values()) {
                            event.setDamage(roomEntityDamageByEntityEvent.getDamage(value), value);
                        }
                    }
                    event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                    event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                }
            }
        }
    }

    @EventHandler
    public void onMotion(EntityMotionEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            Room room = RoomManager.getRoom(player);
            if (room == null) {
                return;
            }
            BasicAttackSetting basicAttackSetting = room.getRoomRule().getBasicAttackSetting();
            if (basicAttackSetting != null) {
                Vector3 v = event.getMotion();
                v.x *= basicAttackSetting.getMotionXZ();
                if (entity.isOnGround()) {
                    v.y *= basicAttackSetting.getMotionY();
                } else {
                    v.y *= basicAttackSetting.getAirMotionY();
                }

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

    @EventHandler(priority = EventPriority.MONITOR)
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

    @EventHandler(priority = EventPriority.MONITOR)
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
                if (!room.getPlayLevels().contains(toLevel)) {
                    if (room.getPlayLevels().contains(fromLevel)) {
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
                                    targetRoom.addPlayer(player, JoinRoomReason.TELEPORT);
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
                    room1.addPlayer(player, JoinRoomReason.TELEPORT);
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

        // 对发送者进行限制
        boolean senderInGame = room != null;
        for (CommandSender commandSender : new ArrayList<>(event.getRecipients())) {
            if (commandSender.isPlayer()) {
                Player receiver = (Player) commandSender;
                boolean remove = false;
                if (HideChatCommand.hideMessagePlayers.contains(receiver)) {
                    remove = true;
                } else {
                    Room targetRoom = RoomManager.getRoom(receiver);
                    // 如果发送消息的玩家不在游戏或不在同一房间，就要开始接下来的判断
                    if ((!senderInGame || room != targetRoom)
                            && targetRoom != null
                            && targetRoom.getRoomRule().isEnableRoomChatSystem()) {
                        remove = true;
                    }
                }
                if (remove) {
                    event.getRecipients().removeIf(o -> o.getName().equals(commandSender.getName()));
                }
            }
        }

        if (room != null) {
            if (room.getRoomRule().isEnableRoomChatSystem()) {
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
    public void EntityInteractEvent(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        for (Room room : RoomManager.getRooms(entity.getLevel())) {
            RoomEntityInteractEvent ev = new RoomEntityInteractEvent(room, entity, event.getBlock());
            GameListenerRegistry.callEvent(room, ev);
            if (ev.isCancelled()) {
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
                    case Item.DIAMOND_SWORD:
                        if (event.getBlock() != null) {
                            player.sendMessage(
                                    "Pos: " + DecimalTools.getDouble(player.getX(), 1, RoundingMode.HALF_UP) + ":" +
                                            DecimalTools.getDouble(player.getY(), 1, RoundingMode.HALF_UP) + ":" +
                                            DecimalTools.getDouble(player.getZ(), 1, RoundingMode.HALF_UP)
                            );
                        }
                        return;
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
                    case Item.GOLDEN_HOE:
                        Block block = event.getBlock();
                        if (block != null) {
                            if (block instanceof BlockTrapdoor blockDoor) {
                                blockDoor.setDamage(blockDoor.getDamage() ^ 8);
                                blockDoor.getLevel().setBlock(blockDoor, blockDoor, true);
                                player.sendMessage("Successfully open the door!");
                            } else if (block instanceof BlockRedstoneLamp blockRedstoneLamp) {
                                if (blockRedstoneLamp.getId() == BlockID.REDSTONE_LAMP) {
                                    block.getLevel().setBlock(blockRedstoneLamp, Block.get(Block.LIT_REDSTONE_LAMP), false, false);
                                } else {
                                    block.getLevel().setBlock(blockRedstoneLamp, Block.get(Block.REDSTONE_LAMP), false, false);
                                }
                            }
                        }
                        break;
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
        PlayerTempStateManager.recoverData(player);
    }

    @EventHandler
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void PlayerChangeSkinEvent(PlayerChangeSkinEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            if (!room.getRoomRule().isAllowChangeSkin()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void BlockFallEvent(BlockFallEvent event) {
        List<Room> rooms = RoomManager.getRooms(event.getBlock().getLevel());
        for (Room room : rooms) {
            RoomBlockFallEvent ev = new RoomBlockFallEvent(room, event.getBlock());
            GameListenerRegistry.callEvent(room, ev);
            if (ev.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void DataPacketReceiveEvent(DataPacketReceiveEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            DataPacket pk = event.getPacket();
            if (event.getPacket().packetId() == ProtocolInfo.MOVE_PLAYER_PACKET) {
                // MovePlayerPacket movePlayerPacket = (MovePlayerPacket) pk;
                Block block = PlayerTools.getBlockUnderPlayer(player);
                if (block == null) {
                    return;
                }
                RoomBlockTreadEvent ev = new RoomBlockTreadEvent(room, block, player);
                GameListenerRegistry.callEvent(room, ev);
            } else if (event.getPacket().packetId() == ProtocolInfo.PLAYER_AUTH_INPUT_PACKET) {
                PlayerAuthInputPacket playerAuthInputPacket = (PlayerAuthInputPacket) pk;
                Vector3 pos = playerAuthInputPacket.getPosition().asVector3();
                int y = player.getLevelBlock().getFloorY();
                Block block = PlayerTools.getBlockUnderPlayer(y, pos, player.getLevel());
                if (block == null) {
                    return;
                }
                RoomBlockTreadEvent ev = new RoomBlockTreadEvent(room, block, player);
                GameListenerRegistry.callEvent(room, ev);
            }
        }
    }

    @EventHandler
    public void PlayerMissedSwingEvent(PlayerMissedSwingEvent event) {
        Room room = RoomManager.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerMissedSwingEvent roomEvent = new RoomPlayerMissedSwingEvent(room, event.getPlayer());
            GameListenerRegistry.callEvent(room, roomEvent);
        }
    }

    @EventHandler
    public void onVehicleEnter(EntityVehicleEnterEvent event) {
        Entity entity = event.getEntity();
        EntityVehicle vehicle = event.getVehicle();
        for (Room room : RoomManager.getRooms(vehicle.getLevel())) {
            RoomEntityVehicleEnterEvent rev = new RoomEntityVehicleEnterEvent(room, entity, vehicle);
            rev.call();
            if (rev.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleExit(EntityVehicleExitEvent event) {
        Entity entity = event.getEntity();
        EntityVehicle vehicle = event.getVehicle();
        for (Room room : RoomManager.getRooms(vehicle.getLevel())) {
            RoomEntityVehicleExitEvent rev = new RoomEntityVehicleExitEvent(room, entity, vehicle);
            rev.call();
            if (rev.isCancelled()) {
                event.setCancelled(true);
            }
        }
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