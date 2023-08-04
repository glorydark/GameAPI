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
import cn.nukkit.level.ParticleEffect;
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
import gameapi.room.team.BaseTeam;
import gameapi.utils.AdvancedLocation;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
public class BaseEventListener implements Listener {

    public static HashMap<String, List<DamageSource>> damageSources = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerLocallyInitializedEvent(PlayerLocallyInitializedEvent event) {
        Player player = event.getPlayer();
        GameAPI.playerRoomHashMap.put(player, null);
        if (player != null) {
            if (GameAPI.saveBag) {
                if (InventoryTools.getPlayerBagConfig(player) != null) {
                    InventoryTools.loadBag(player);
                    player.getFoodData().setLevel(20, 20.0F);
                    Server.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("baseEvent.join.bagCacheExisted", player.getName()));
                }
            }
            //event.getPlayer().setLocale(Locale.US);
            for (TextEntity entity : EntityTools.entityList) {
                entity.spawnTo(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            for (Player p : room.getPlayers()) {
                p.sendMessage(GameAPI.getLanguage().getTranslation(p, "baseEvent.quit.roomQuit", player.getName()));
            }
            room.removePlayer(player, true);
        }
        GameAPI.editDataHashMap.remove(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
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
                    }else {
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
                }else {
                    event.setDropExp(roomBlockBreakEvent.getDropExp());
                    event.setInstaBreak(roomBlockBreakEvent.isInstaBreak());
                    event.setDrops(roomBlockBreakEvent.getDrops());
                }
            }
        }else{
            if(GameAPI.editDataHashMap.containsKey(player) && GameAPI.editDataHashMap.get(player) != null){
                GameAPI.editDataHashMap.get(player).respondEvent(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
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
        }else{
            if(GameAPI.editDataHashMap.containsKey(player) && GameAPI.editDataHashMap.get(player) != null){
                GameAPI.editDataHashMap.get(player).respondEvent(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                if (room.getRoomRule().isAllowDropItem()) {
                    event.setCancelled(true);
                }else{
                    RoomPlayerDropItemEvent roomPlayerDropItemEvent = new RoomPlayerDropItemEvent(room, player, event.getItem());
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
        GameAPI.loadedRooms.forEach((s, rooms) -> roomList.addAll(rooms));
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
                }else{
                    if(!room.getRoomRule().isAllowExplosionBreakBlock()){
                        event.setBlockList(new ArrayList<>());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodePrimeEvent(ExplosionPrimeEvent event) {
        List<Room> roomList = new ArrayList<>();
        GameAPI.loadedRooms.forEach((s, rooms) -> roomList.addAll(rooms));
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
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart && !room.getRoomRule().isAllowReadyStartWalk()) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getFloorX() != to.getFloorX() || from.getFloorZ() != to.getFloorZ()) {
                    Location newTo = new Location(from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch(), from.getHeadYaw());
                    event.setTo(newTo);
                }
            }

            if(GameAPI.updateMoveEvent){
                RoomPlayerMoveEvent roomPlayerMoveEvent = new RoomPlayerMoveEvent(room, player, event.getFrom(), event.getTo(), event.isResetBlocksAround());
                if(roomPlayerMoveEvent.isCancelled()){
                    event.setCancelled(true);
                }else{
                    event.setTo(roomPlayerMoveEvent.getTo());
                    event.setFrom(roomPlayerMoveEvent.getFrom());
                    event.setResetBlocksAround(roomPlayerMoveEvent.isResetBlocksAround());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Room room = Room.getRoom((Player) entity);
            if (room == null) {
                return;
            }
            if (entity.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(room, (Player) entity, event.getCause());
                    //Server.getInstance().getPluginManager().callEvent(ev);
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        entity.setHealth(entity.getMaxHealth());
                        room.setSpectator((Player) entity, room.getRoomRule().getSpectatorGameMode(), true);
                        damageSources.remove(entity.getName());
                    }
                } else {
                    entity.setHealth(entity.getMaxHealth());
                    room.setSpectator((Player) entity, room.getRoomRule().getSpectatorGameMode(), false);
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
            if (!room1.getRoomRule().isAllowDamagePlayer() || room1.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
                return;
            }
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player p1 = (Player) event.getEntity();
                Player p2 = (Player) event.getDamager();
                if (room1.getTeams().size() > 0) {
                    if (room1.getPlayerTeam(p1) != null && room1.getPlayerTeam(p1) == room2.getPlayerTeam(p2)) {
                        p1.sendMessage(GameAPI.getLanguage().getTranslation(p1,"baseEvent.teamDamage.notAllowed"));
                        event.setCancelled(true);
                    } else {
                        addDamageSource(p1.getName(), p2.getName());
                    }
                } else {
                    addDamageSource(p1.getName(), p2.getName());
                }
            }
            RoomEntityDamageByEntityEvent roomEntityDamageByEntityEvent = new RoomEntityDamageByEntityEvent(room1, event);
            GameListenerRegistry.callEvent(room1, roomEntityDamageByEntityEvent);
            if (roomEntityDamageByEntityEvent.isCancelled()) {
                event.setCancelled(true);
            } else {
                if(room1.getRoomRule().isExperimentalFeature()){
                    if(event.getDamager() instanceof Player){
                        Player damagePlayer = (Player) event.getDamager();
                        if(damagePlayer.fallDistance >= 0.5 && damagePlayer.getInventory().getItemInHand().isAxe()){
                            entity.getLevel().addParticleEffect(entity.getPosition(), ParticleEffect.CRITICAL_HIT);
                            roomEntityDamageByEntityEvent.setDamage(roomEntityDamageByEntityEvent.getDamage() * 1.5f);
                        }
                    }
                }
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
            Room room = Room.getRoom(player);
            if (room != null) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.commandExecute.notAllowed"));
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
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "baseEvent.levelChange.notAllowed"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void CraftItemEvent(CraftItemEvent event) {
        Player player = event.getPlayer();
        if(player == null){
            return;
        }
        Room room = Room.getRoom(player);
        if (room != null) {
            if(!room.getRoomRule().isAllowCraft()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntityLevelChangeEvent(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            for (TextEntity entity : EntityTools.entityList) {
                if(entity.getLevel() == event.getEntity().getLevel()){
                    entity.spawnTo((Player) event.getEntity());
                }else{
                    entity.despawnFrom((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void PlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room == null) {
            // Player is not in game, so the server will send the message to the players who are not in game.
            event.setRecipients(Server.getInstance().getOnlinePlayers().values().stream()
                    .filter(p -> GameAPI.playerRoomHashMap.get(p) == null).collect(Collectors.toSet()));
            return;
        }
        // Player is in game, so we trigger RoomPlayerChatEvent.
        RoomPlayerChatEvent chatEvent = new RoomPlayerChatEvent(room, player, new RoomChatData(player.getName(), event.getMessage()));
        GameListenerRegistry.callEvent(room, chatEvent);
        if (!chatEvent.isCancelled()) {
            String msg = GameAPI.getLanguage().getTranslation(player, "baseEvent.chat.message_format", room.getRoomName(), chatEvent.getRoomChatData().getDefaultChatMsg());
            if(msg.startsWith("@")){
                if(room.getTeams().size() > 0){
                    BaseTeam team = room.getPlayerTeam(player);
                    if(team != null){
                        team.sendMessageToAll(msg.replaceFirst("@", ""));
                    }
                }else{
                    room.sendMessageToAll(msg);
                }
            }else{
                room.sendMessageToAll(msg);
            }
        }
        event.setCancelled(true);
    }

    // Extra listener for GameListeners
    @EventHandler
    public void PlayerJumpEvent(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            RoomPlayerJumpEvent roomPlayerJumpEvent = new RoomPlayerJumpEvent(room, player);
            GameListenerRegistry.callEvent(room, roomPlayerJumpEvent);
        }
    }

    @EventHandler
    public void PlayerToggleGlideEvent(PlayerToggleGlideEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
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
        Room room = Room.getRoom(player);
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
        Room room = Room.getRoom(player);
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
        Room room = Room.getRoom(player);
        if (room != null) {
            RoomPlayerInteractEvent roomPlayerInteractEvent = new RoomPlayerInteractEvent(room, player, event.getBlock(), event.getTouchVector(), event.getFace(), event.getItem(), event.getAction());
            GameListenerRegistry.callEvent(room, roomPlayerInteractEvent);
            if(roomPlayerInteractEvent.isCancelled()){
                event.setCancelled(true);
            }
        }else{
            if (GameAPI.editDataHashMap.containsKey(player) && GameAPI.editDataHashMap.get(player) != null){
                GameAPI.editDataHashMap.get(player).respondEvent(event);
            }
        }
    }

    @EventHandler
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            RoomPlayerItemConsumeEvent roomPlayerItemConsumeEvent = new RoomPlayerItemConsumeEvent(room, player, event.getItem());
            GameListenerRegistry.callEvent(room, roomPlayerItemConsumeEvent);
            if(roomPlayerItemConsumeEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Room room = Room.getRoom(player);
        if (room != null) {
            RoomPlayerItemHeldEvent roomPlayerItemHeldEvent = new RoomPlayerItemHeldEvent(room, player, event.getItem());
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