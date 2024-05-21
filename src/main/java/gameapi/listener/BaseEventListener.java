package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
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
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.commands.WorldEditCommand;
import gameapi.entity.GameProjectileEntity;
import gameapi.event.block.RoomBlockBreakEvent;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.entity.*;
import gameapi.event.player.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.room.RoomVirtualHealthManager;
import gameapi.room.Room;
import gameapi.room.RoomChatData;
import gameapi.room.RoomStatus;
import gameapi.room.edit.EditData;
import gameapi.room.items.RoomItemBase;
import gameapi.room.team.BaseTeam;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.PosSet;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
public class BaseEventListener implements Listener {

    public static HashMap<String, List<DamageSource>> damageSources = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Room room = RoomManager.getRoom(player);
        if (room != null) {
            for (Player p : room.getPlayers()) {
                p.sendMessage(GameAPI.getLanguage().getTranslation(p, "baseEvent.quit.success", player.getName()));
            }
            if (room.getPlayers().contains(player)) {
                room.removePlayer(player);
            } else {
                room.removeSpectator(player);
            }
        } else {
            for (EditData editData : GameAPI.editDataList) {
                Player editor = editData.getPlayer();
                if (editor == player) {
                    editData.onQuit();
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
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
            }
            if (!room.getRoomRule().isAllowBreakBlock()) {
                if (!room.getRoomRule().getAllowBreakBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage())) {
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
                if (block == null) {
                    return;
                }
                if (GameAPI.worldEditPlayers.contains(player)) {
                    if (block.getId() != 0) {
                        player.sendMessage("方块: " + block.toItem().getNamespaceId() + ", 位置: " + block.getFloorX() + ":" + block.getFloorY() + ":" + block.getFloorZ());
                        return;
                    }
                }
            }
            for (EditData editData : GameAPI.editDataList) {
                Player editor = editData.getPlayer();
                if (editor == player) {
                    editData.getCurrentStep().onBreak(editData);
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
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
            } else {
                if (!room.getRoomRule().isAllowPlaceBlock()) {
                    if (!room.getRoomRule().getAllowPlaceBlocks().contains(event.getBlock().getId() + ":" + event.getBlock().getDamage())) {
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
                        return;
                }
            } else {
                for (EditData editData : GameAPI.editDataList) {
                    Player editor = editData.getPlayer();
                    if (editor == player) {
                        editData.getCurrentStep().onPlace(editData);
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
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                if (room.getRoomRule().isAllowDropItem()) {
                    event.setCancelled(true);
                } else {
                    RoomPlayerDropItemEvent roomPlayerDropItemEvent = new RoomPlayerDropItemEvent(room, player, event.getItem());
                    GameListenerRegistry.callEvent(room, roomPlayerDropItemEvent);
                    if (roomPlayerDropItemEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodeEvent(EntityExplodeEvent event) {
        List<Room> roomList = new ArrayList<>();
        RoomManager.loadedRooms.forEach((s, rooms) -> roomList.addAll(rooms));
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
        List<Room> roomList = new ArrayList<>();
        RoomManager.loadedRooms.forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                for (AdvancedLocation location : room.getStartSpawn()) {
                    if (location.getLevel().getName().equals(event.getEntity().level.getName())) {
                        return;
                    }
                }
                if (!room.getRoomRule().isAllowExplosion()) {
                    event.getEntity().kill();
                    event.setCancelled(true);
                }
                RoomEntityExplodeEvent roomEntityExplodeEvent = new RoomEntityExplodeEvent(event.getEntity(), event.getForce());
                GameListenerRegistry.callEvent(room, roomEntityExplodeEvent);
                if (!roomEntityExplodeEvent.isCancelled()) {
                    event.setForce(roomEntityExplodeEvent.getForce());

                } else {
                    event.getEntity().kill();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityDamageEvent(EntityDamageEvent event) {
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
        if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setDamage(0);
                player.teleport(player.getLevel().getSpawnLocation(), null);
            }
            event.setCancelled(true);
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
                    event.setDamage(0);
                    return;
                }
                break;
            case BLOCK_EXPLOSION:
                if (!room.getRoomRule().isAllowBlockExplosionDamage()) {
                    event.setDamage(0);
                    return;
                }
                break;
        }
        if (room.getRoomRule().isVirtualHealth()) {
            RoomVirtualHealthManager manager = room.getRoomVirtualHealthManager();
            if (manager.getHealth(player) - event.getFinalDamage() <= 0) {
                event.setDamage(0);
                if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(room, (Player) entity, event.getCause());
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        manager.setHealth(player, manager.getMaxHealth());
                        damageSources.remove(entity.getName());
                        if (room.getRoomRule().isAllowRespawn()) {
                            int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                            room.setDeath(player);
                            room.addRespawnTask(player, respawnTicks);
                        } else {
                            room.setDeath(player);
                        }
                    }
                } else {
                    manager.setHealth(player, manager.getMaxHealth());
                }
            } else {
                if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (room.getRoomRule().isAllowRespawn()) {
                        int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                        room.setDeath(player);
                        room.addRespawnTask(player, respawnTicks);
                    } else {
                        room.setDeath(player);
                    }
                }
            }
        } else {
            if (entity.getHealth() - event.getFinalDamage() <= 0) {
                if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(room, (Player) entity, event.getCause());
                    //Server.getInstance().getPluginManager().callEvent(ev);
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        entity.setHealth(entity.getMaxHealth());
                        damageSources.remove(entity.getName());
                        if (room.getRoomRule().isAllowRespawn()) {
                            int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                            room.setDeath(player);
                            room.addRespawnTask(player, respawnTicks);
                        } else {
                            room.setDeath(player);
                        }
                    }
                } else {
                    entity.setHealth(entity.getMaxHealth());
                }
            } else {
                if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (room.getRoomRule().isAllowRespawn()) {
                        int respawnTicks = room.getRoomRule().getRespawnCoolDownTick();
                        room.setDeath(player);
                        room.addRespawnTask(player, respawnTicks);
                    } else {
                        room.setDeath(player);
                    }
                }
            }
        }
        if (event.isCancelled()) {
            return;
        }
        RoomEntityDamageEvent roomEntityDamageEvent = new RoomEntityDamageEvent(room, entity, event.getCause(), event.getFinalDamage());
        GameListenerRegistry.callEvent(room, roomEntityDamageEvent);
        if (roomEntityDamageEvent.isCancelled()) {
            event.setCancelled(true);
        } else {
            if (room.getRoomRule().isVirtualHealth()) {
                event.setDamage(0f);
                room.getRoomVirtualHealthManager().reduceHealth((Player) entity, BigDecimal.valueOf(roomEntityDamageEvent.getDamage()).doubleValue());
            } else {
                event.setDamage(roomEntityDamageEvent.getDamage());
            }
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
        if (!roomOptional.isPresent()) {
            return;
        }
        Room room1 = roomOptional.get();
        if (entity instanceof Player && damager instanceof Player) {
            Room room2 = RoomManager.getRoom((Player) damager);
            if (room1 == room2) {
                if (room1.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    Player p1 = (Player) event.getEntity();
                    Player p2 = (Player) event.getDamager();
                    RoomPlayerInteractPlayerEvent roomPlayerInteractPlayerEvent = new RoomPlayerInteractPlayerEvent(room1, p2, p1);
                    GameListenerRegistry.callEvent(room1, roomPlayerInteractPlayerEvent);
                    if (!room1.getRoomRule().isAllowDamagePlayer() || room1.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                        event.setCancelled(true);
                        return;
                    }
                    if (room1.getTeams().size() > 0) {
                        if (room1.getPlayerTeam(p1) != null && room1.getPlayerTeam(p1) == room2.getPlayerTeam(p2)) {
                            p1.sendMessage(GameAPI.getLanguage().getTranslation(p1, "baseEvent.team_damage.not_allowed"));
                            event.setCancelled(true);
                            return;
                        }
                    }
                    addDamageSource(p1.getName(), p2.getName());
                }
            }
        }
        RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, event.getEntity(), event.getDamager(), event.getDamage(), event.getAttackCooldown(), event.getKnockBack(), event.getCause());
        GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
        if (roomEntityDamageByEntityEvent.isCancelled()) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof Player && room1.getRoomRule().isVirtualHealth()) {
                event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
                room1.getRoomVirtualHealthManager().reduceHealth((Player) event.getEntity(), BigDecimal.valueOf(roomEntityDamageByEntityEvent.getDamage()).doubleValue());
                event.setDamage(0);
            } else {
                event.setDamage(roomEntityDamageByEntityEvent.getDamage());
                event.setKnockBack(roomEntityDamageByEntityEvent.getKnockBack());
                event.setAttackCooldown(roomEntityDamageByEntityEvent.getAttackCoolDown());
            }
        }
    }

    public void addDamageSource(String player, String damager) {
        List<DamageSource> temp = new ArrayList<>(damageSources.getOrDefault(player, new ArrayList<>()));
        temp.add(new DamageSource(damager, System.currentTimeMillis()));
        damageSources.put(player, temp);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/gameapi")) {
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
        if (player == null || fromLevel == null || toLevel == null) {
            return;
        }
        Room room = RoomManager.getRoom(player);
        if (room != null && event.getCause() != null) {
            if (!fromLevel.equals(toLevel)) {
                List<Level> arenas = new LinkedList<>();
                arenas.add(room.getWaitSpawn().getLevel());
                room.getStartSpawn().forEach(spawn -> arenas.add(spawn.getLevel()));
                if (!arenas.contains(fromLevel) && !arenas.contains(toLevel)) {
                    event.setCancelled(true);
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.level_change.not_allowed"));
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
            event.setRecipients(Server.getInstance().getOnlinePlayers().values().stream()
                    .filter(p -> RoomManager.playerRoomHashMap.get(p) == null).collect(Collectors.toSet()));
            return;
        }
        // Player is in game, so we trigger RoomPlayerChatEvent.
        RoomPlayerChatEvent chatEvent = new RoomPlayerChatEvent(room, player, new RoomChatData(player.getName(), event.getMessage()));
        GameListenerRegistry.callEvent(room, chatEvent);
        if (!chatEvent.isCancelled()) {
            RoomChatData chatData = chatEvent.getRoomChatData();
            String rawMsg = chatData.getMessage();
            if (rawMsg.startsWith("@") && !rawMsg.equals("@")) {
                if (room.getTeams().size() > 0) {
                    BaseTeam team = room.getPlayerTeam(player);
                    if (team != null) {
                        rawMsg = rawMsg.replaceFirst("@", "");
                        String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), rawMsg);
                        team.sendMessageToAll(msg);
                    }
                } else {
                    String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), chatData.getDefaultChatMsg());
                    room.sendMessageToAll(msg);
                }
            } else if (rawMsg.startsWith("!") && !rawMsg.equals("!")) {
                chatData.setMessage(rawMsg.replaceFirst("!", ""));
                for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                    String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), chatData.getDefaultChatMsg());
                    value.sendMessage(msg);
                }
            } else {
                String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), chatData.getDefaultChatMsg());
                room.sendMessageToAll(msg);
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
            RoomPlayerInteractEvent roomPlayerInteractEvent = new RoomPlayerInteractEvent(room, player, event.getBlock(), event.getTouchVector(), event.getFace(), event.getItem(), event.getAction());
            RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(player.getInventory().getItemInHand()));
            if (roomItemBase != null) {
                roomItemBase.executeInteract(roomPlayerInteractEvent);
            } else {
                GameListenerRegistry.callEvent(room, roomPlayerInteractEvent);
                if (roomPlayerInteractEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        } else {
            for (EditData editData : GameAPI.editDataList) {
                Player editor = editData.getPlayer();
                if (editor == player) {
                    editData.getCurrentStep().onInteract(editData);
                }
                break;
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
        if (room != null) {
            RoomPlayerItemHeldEvent roomPlayerItemHeldEvent = new RoomPlayerItemHeldEvent(room, player, event.getItem());
            RoomItemBase roomItemBase = room.getRoomItem(RoomItemBase.getRoomItemIdentifier(player.getInventory().getItemInHand()));
            if (roomItemBase != null) {
                roomItemBase.executeHeldItem(roomPlayerItemHeldEvent);
            } else {
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
        RoomEntityRegainHealthEvent roomEntityRegainHealthEvent = new RoomEntityRegainHealthEvent(event.getEntity(), event.getAmount(), event.getRegainReason());
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
    public void EntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving) entity;
            Room room;
            Optional<Room> roomOptional = RoomManager.getRoom(entity.getLevel());
            if (!roomOptional.isPresent()) {
                return;
            }
            room = roomOptional.get();
            RoomEntityDeathEvent entityDeathEvent = new RoomEntityDeathEvent(room, entityLiving, event.getDrops());
            GameListenerRegistry.callEvent(room, entityDeathEvent);
            if (entityDeathEvent.isCancelled()) {
                event.setCancelled(true);
            } else {
                event.setDrops(entityDeathEvent.getDrops());
            }
        }
    }

    @Data
    public static class DamageSource {
        private String damager;
        private long milliseconds;

        public DamageSource(String damager, long milliseconds) {
            this.damager = damager;
            this.milliseconds = milliseconds;
        }
    }

}