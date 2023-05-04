package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.entity.EntityTools;
import gameapi.entity.GameProjectileEntity;
import gameapi.entity.TextEntity;
import gameapi.event.block.RoomBlockBreakEvent;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.entity.RoomEntityDamageByEntityEvent;
import gameapi.event.entity.RoomProjectileHitEvent;
import gameapi.event.entity.RoomProjectileLaunchEvent;
import gameapi.event.player.*;
import gameapi.inventory.InventoryTools;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomChatData;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
public class PlayerEventListener implements Listener {

    public static HashMap<String, List<DamageSource>> damageSources = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerLocallyInitializedEvent(PlayerLocallyInitializedEvent event) {
        GameAPI.playerRoomHashMap.put(event.getPlayer(), null);
        if (event.getPlayer() != null) {
            if (GameAPI.saveBag) {
                if (InventoryTools.getPlayerBagConfig(event.getPlayer()) != null) {
                    InventoryTools.loadBag(event.getPlayer());
                    event.getPlayer().getFoodData().setLevel(20, 20.0F);
                    Server.getInstance().getLogger().info("检测到玩家" + event.getPlayer().getName() + "背包上次未能返还，已经返还！");
                }
            }
            event.getPlayer().setLocale(Locale.US);
            for (TextEntity entity : EntityTools.entityList) {
                entity.spawnTo(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            for (Player p : room.getPlayers()) {
                p.sendMessage("玩家" + event.getPlayer().getName() + "退出了本房间");
            }
            room.removePlayer(event.getPlayer(), true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
            }
            if (!room.getRoomRule().allowBreakBlock) {
                if (!room.getRoomRule().canBreakBlocks.contains(event.getBlock().getId() + ":" + event.getBlock().getDamage())) {
                    event.setCancelled(true);
                } else {
                    RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, event.getBlock(), event.getPlayer(), event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                    GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                    if (roomBlockBreakEvent.isCancelled()) {
                        event.setCancelled(true);
                    }else {
                        event.setDropExp(roomBlockBreakEvent.getBlockXP());
                        event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                        event.setDrops(roomBlockBreakEvent.getBlockDrops());
                    }
                }
            } else {
                RoomBlockBreakEvent roomBlockBreakEvent = new RoomBlockBreakEvent(room, event.getBlock(), event.getPlayer(), event.getItem(), event.getInstaBreak(), event.getDrops(), event.getDropExp(), event.getFace());
                GameListenerRegistry.callEvent(room, roomBlockBreakEvent);
                if (roomBlockBreakEvent.isCancelled()) {
                    event.setCancelled(true);
                }else {
                    event.setDropExp(roomBlockBreakEvent.getBlockXP());
                    event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                    event.setDrops(roomBlockBreakEvent.getBlockDrops());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
            } else {
                if (!room.getRoomRule().allowPlaceBlock) {
                    if (!room.getRoomRule().canPlaceBlocks.contains(event.getBlock().getId() + ":" + event.getBlock().getDamage())) {
                        event.setCancelled(true);
                    } else {
                        RoomBlockPlaceEvent roomBlockPlaceEvent = new RoomBlockPlaceEvent(room, event.getBlock(), event.getPlayer(), event.getItem(), event.getBlockAgainst(), event.getBlockReplace());
                        GameListenerRegistry.callEvent(room, roomBlockPlaceEvent);
                        if (roomBlockPlaceEvent.isCancelled()) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    RoomBlockPlaceEvent roomBlockPlaceEvent = new RoomBlockPlaceEvent(room, event.getBlock(), event.getPlayer(), event.getItem(), event.getBlockAgainst(), event.getBlockReplace());
                    GameListenerRegistry.callEvent(room, roomBlockPlaceEvent);
                    if (roomBlockPlaceEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                if (room.getRoomRule().noDropItem) {
                    event.setCancelled(true);
                }else{
                    RoomPlayerDropItemEvent roomPlayerDropItemEvent = new RoomPlayerDropItemEvent(room, event.getPlayer(), event.getItem());
                    GameListenerRegistry.callEvent(room, roomPlayerDropItemEvent);
                    if(roomPlayerDropItemEvent.isCancelled()){
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
        GameAPI.RoomHashMap.forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                for (AdvancedLocation location : room.getStartSpawn()) {
                    if (location.getLevel().getName().equals(event.getPosition().level.getName())) {
                        return;
                    }
                }
                if (room.getRoomRule().antiExplosion) {
                    event.getEntity().kill();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodePrimeEvent(ExplosionPrimeEvent event) {
        List<Room> roomList = new ArrayList<>();
        GameAPI.RoomHashMap.forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room : roomList) {
            if (room != null) {
                for (AdvancedLocation location : room.getStartSpawn()) {
                    if (location.getLevel().getName().equals(event.getEntity().level.getName())) {
                        return;
                    }
                }
                if (room.getRoomRule().antiExplosion) {
                    event.getEntity().kill();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart && room.getRoomRule().noStartWalk) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getFloorX() != to.getFloorX() || from.getFloorZ() != to.getFloorZ()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Room room1 = Room.getRoom((Player) entity);
            if (room1 == null) {
                return;
            }
            if (entity.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                if (room1.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(room1, (Player) entity, event.getCause());
                    //Server.getInstance().getPluginManager().callEvent(ev);
                    GameListenerRegistry.callEvent(room1.getGameName(), ev);
                    if (!ev.isCancelled()) {
                        entity.setHealth(entity.getMaxHealth());
                        room1.setSpectator((Player) entity, room1.getRoomRule().allowSpectatorMode, true);
                        damageSources.remove(entity.getName());
                    }
                } else {
                    entity.setHealth(entity.getMaxHealth());
                    room1.setSpectator((Player) entity, true, false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            Room room1 = Room.getRoom((Player) entity);
            Room room2 = Room.getRoom((Player) damager);
            if (room1 != room2) {
                return;
            }
            if (room1 == null) {
                return;
            }
            if (!room1.getRoomRule().allowDamagePlayer || room1.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
                return;
            }
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player p1 = (Player) event.getEntity();
                Player p2 = (Player) event.getDamager();
                Room r1 = Room.getRoom(p1);
                Room r2 = Room.getRoom(p2);
                if (r1 != null && r2 != null) {
                    if (r1.getTeams().size() > 0) {
                        if (r1.getPlayerTeam(p1) != null && r1.getPlayerTeam(p1) == r1.getPlayerTeam(p2)) {
                            p1.sendMessage("§c你不能攻击你的队友");
                            event.setCancelled(true);
                        } else {
                            addDamageSource(p1.getName(), p2.getName());
                        }
                    } else {
                        addDamageSource(p1.getName(), p2.getName());
                    }
                }
            }
            RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, event);
            GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
            if (roomEntityDamageByEntityEvent.isCancelled()) {
                event.setCancelled(true);
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

    @Data
    public static class DamageSource {
        private String damager;
        private long milliseconds;

        public DamageSource(String damager, long milliseconds) {
            this.damager = damager;
            this.milliseconds = milliseconds;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (player.isOp()) {
                return;
            }
            Room room = Room.getRoom(player);
            if (room != null) {
                player.sendMessage(TextFormat.RED + "游戏内玩家不可执行指令！");
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
        Room room = Room.getRoom(player);
        if (room != null) {
            if (!fromLevel.equals(toLevel)) {
                List<Level> arenas = new LinkedList<>();
                arenas.add(room.getWaitSpawn().getLevel());
                room.getStartSpawn().forEach(spawn -> arenas.add(spawn.getLevel()));
                if (!arenas.contains(fromLevel) && !arenas.contains(toLevel)) {
                    event.setCancelled(true);
                    player.sendMessage("§c请使用命令加入/退出游戏房间！");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) //暂时合成设置不可使用
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && Room.getRoom(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && Room.getRoom(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }
     */

    @EventHandler
    public void FlyEvent(PlayerToggleFlightEvent event) {
        if (Room.getRoom(event.getPlayer()) == null) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        if (Room.getRoom(event.getPlayer()) == null) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityLevelChangeEvent(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            for (TextEntity entity : EntityTools.entityList) {
                entity.spawnTo((Player) event.getEntity());
            }
        }
    }

    @EventHandler
    public void PlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room == null) {
            // Player is not in game, so the server will send the message to the players who are not in game.
            List<Player> gamePlayers = new ArrayList<>(GameAPI.playerRoomHashMap.keySet());
            event.setRecipients(Server.getInstance().getOnlinePlayers().values().stream()
                    .filter(p -> GameAPI.playerRoomHashMap.get(p) == null).collect(Collectors.toSet()));
            return;
        }
        // Player is in game, so we trigger RoomPlayerChatEvent.
        RoomPlayerChatEvent chatEvent = new RoomPlayerChatEvent(room, player, new RoomChatData(player.getName(), event.getMessage()));
        GameListenerRegistry.callEvent(room, chatEvent);
        if (!chatEvent.isCancelled()) {
            String msg = "[" + room.getRoomName() + "] " + chatEvent.getRoomChatData().getDefaultChatMsg();
            for (Player roomPlayer : room.getPlayers()) {
                roomPlayer.sendMessage(msg);
            }
        }
        event.setCancelled(true);
    }

    // Extra listener for GameListeners
    @EventHandler
    public void PlayerJumpEvent(PlayerJumpEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerJumpEvent roomPlayerJumpEvent = new RoomPlayerJumpEvent(room, event.getPlayer());
            GameListenerRegistry.callEvent(room, roomPlayerJumpEvent);
        }
    }

    @EventHandler
    public void PlayerToggleGlideEvent(PlayerToggleGlideEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerToggleGlideEvent roomPlayerToggleGlideEvent = new RoomPlayerToggleGlideEvent(room, event.getPlayer(), event.isGliding());
            GameListenerRegistry.callEvent(room, roomPlayerToggleGlideEvent);
            if (roomPlayerToggleGlideEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerToggleSneakEvent roomPlayerToggleSneakEvent = new RoomPlayerToggleSneakEvent(room, event.getPlayer(), event.isSneaking());
            GameListenerRegistry.callEvent(room, roomPlayerToggleSneakEvent);
            if (roomPlayerToggleSneakEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerToggleSprintEvent roomPlayerToggleSprintEvent = new RoomPlayerToggleSprintEvent(room, event.getPlayer(), event.isSprinting());
            GameListenerRegistry.callEvent(room, roomPlayerToggleSprintEvent);
            if (roomPlayerToggleSprintEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerInteractEvent roomPlayerInteractEvent = new RoomPlayerInteractEvent(room, event.getPlayer(), event.getBlock(), event.getTouchVector(), event.getFace(), event.getItem(), event.getAction());
            GameListenerRegistry.callEvent(room, roomPlayerInteractEvent);
            if(roomPlayerInteractEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerItemConsumeEvent roomPlayerItemConsumeEvent = new RoomPlayerItemConsumeEvent(room, event.getPlayer(), event.getItem());
            GameListenerRegistry.callEvent(room, roomPlayerItemConsumeEvent);
            if(roomPlayerItemConsumeEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        Room room = Room.getRoom(event.getPlayer());
        if (room != null) {
            RoomPlayerItemHeldEvent roomPlayerItemHeldEvent = new RoomPlayerItemHeldEvent(room, event.getPlayer(), event.getItem());
            GameListenerRegistry.callEvent(room, roomPlayerItemHeldEvent);
            if(roomPlayerItemHeldEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event){
        if(event.getEntity() instanceof GameProjectileEntity){
            Room room = ((GameProjectileEntity) event.getEntity()).getRoom();
            RoomProjectileHitEvent roomProjectileHitEvent = new RoomProjectileHitEvent(room, event.getEntity(), event.getMovingObjectPosition());
            GameListenerRegistry.callEvent(room, roomProjectileHitEvent);
            if(roomProjectileHitEvent.isCancelled()){
                event.setCancelled(true);
            }else{
                event.setMovingObjectPosition(roomProjectileHitEvent.getMovingObjectPosition());
            }
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event){
        if(event.getEntity() instanceof GameProjectileEntity){
            Room room = ((GameProjectileEntity) event.getEntity()).getRoom();
            RoomProjectileLaunchEvent roomProjectileLaunchEvent = new RoomProjectileLaunchEvent(room, event.getEntity());
            GameListenerRegistry.callEvent(room, roomProjectileLaunchEvent);
            if(roomProjectileLaunchEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

}