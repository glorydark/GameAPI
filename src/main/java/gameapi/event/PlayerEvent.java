package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import gameapi.MainClass;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlayerEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void Join(PlayerLocallyInitializedEvent event){
        if(event.getPlayer() != null) {
            if (Inventory.getPlayerBagConfig(event.getPlayer()) != null) {
                Inventory.loadBag(event.getPlayer());
                Server.getInstance().getLogger().info("检测到玩家"+event.getPlayer().getName()+"背包上次未能返还，已经返还！");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void Exit(PlayerQuitEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            for (Player p : room.getPlayers()) {
                p.sendMessage("玩家" + event.getPlayer().getName() + "退出了本房间");
            }
            room.removePlayer(event.getPlayer(),false);
            if(room.getPlayers().size() < 1){
                room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                event.setCancelled(true);
            }
            if(!room.getRoomRule().allowBreakBlock) {
                if (!room.getRoomRule().canBreakBlocks.contains(event.getBlock().getId())) {
                    event.setCancelled(true);
                }
                /*else {
                    room.addBreakBlock(event.getBlock());
                }

                 */
            }
            /*else {
                room.addBreakBlock(event.getBlock());
            }
             */
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                event.setCancelled(true);
            }
            if(!room.getRoomRule().allowPlaceBlock) {
                if (!room.getRoomRule().canPlaceBlocks.contains(event.getBlock().getId())) {
                    event.setCancelled(true);
                }
                /*else{
                    room.addPlaceBlock(event.getBlock());
                }
                 */
            }
            /*else{
                room.addPlaceBlock(event.getBlock());
            }*/
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        for (Room room:MainClass.RoomHashMap){
            if(room.getPlayers().contains(event.getPlayer())) {
                if (room.getRoomRule().noDropItem) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodeEvent(EntityExplodeEvent event){
        Room room = Room.getRoom(event.getPosition().level);
        if(room != null){
            if(room.getRoomRule().antiExplosion){
                event.getEntity().kill();
                event.setCancelled(true);
            }
            /*else {
                room.addBreakBlocks(event.getBlockList());
            }*/
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodePrimeEvent(ExplosionPrimeEvent event){
        Room room = Room.getRoom(event.getEntity().getLevel());
        if(room != null) {
            if (room.getRoomRule().antiExplosion) {
                event.getEntity().kill();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMoveEvent(PlayerMoveEvent event){
        if(Room.getRoom(event.getPlayer()) != null) {
            for (Room room:MainClass.RoomHashMap){
                for(Player player: room.getPlayers()){
                    if(player == event.getPlayer()){
                        if(room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart && room.getRoomRule().startNoWalk){
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        if(entity instanceof Player){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (Room.getRoom(player) != null) {
            if(!Room.getRoom(player).getRoomRule().allowFoodLevelChange) {
                if (event.getFoodLevel() < player.getFoodData().getLevel() ||
                        event.getFoodSaturationLevel() < player.getFoodData().getFoodSaturationLevel()) {
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamageEvent(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if(entity != null && damager != null) {
            if (entity instanceof Player && damager instanceof Player) {
                Room room1 = Room.getRoom((Player) entity);
                Room room2 = Room.getRoom((Player) damager);
                if(room1 != null && room2 != null && !room1.getRoomRule().allowDamagePlayer) {
                    event.setDamage(0);
                    event.setKnockBack(0);
                    event.setCancelled(true);
                }
            } else {
                if (entity instanceof Player) {
                    Player player = ((Player) entity).getPlayer();
                    Room room = Room.getRoom(player);
                    if(room != null) {
                        switch (event.getCause()) {
                            case ENTITY_EXPLOSION:
                                if (!room.getRoomRule().allowEntityExplosionDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case MAGIC:
                                if (!room.getRoomRule().allowMagicDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case FIRE:
                            case LAVA:
                            case FIRE_TICK:
                                if (!room.getRoomRule().allowFireDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case VOID:
                                for(Player p:room.getPlayers()){
                                    p.sendMessage(player.getName() + "掉进了虚空");
                                }
                                room.setSpectatorMode(player);
                                break;
                            case HUNGER:
                                if (!room.getRoomRule().allowHungerDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case DROWNING:
                                if (!room.getRoomRule().allowDrowningDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case LIGHTNING:
                                if (!room.getRoomRule().allowLightningDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case BLOCK_EXPLOSION:
                                if (!room.getRoomRule().allowBlockExplosionDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case FALL:
                                if (!room.getRoomRule().allowFallDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case PROJECTILE:
                                if (!room.getRoomRule().allowProjectTileDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                            case SUFFOCATION:
                                if (!room.getRoomRule().allowSuffocationDamage) {
                                    event.setCancelled(true);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void SlotChangeEvent(InventoryMoveItemEvent event){
        Item item = event.getItem();
        if(item.hasCompoundTag()){
            if(item.getNamedTag().contains("ItemType")){
                if(item.getNamedTag().getString("ItemType").equals("skillItem")){
                    Set players = event.getInventory().getViewers();
                    if(players.size() > 0) {
                        for(Object player : players){
                            Player p = (Player) player;
                            if(p != null){
                                p.getInventory().setItem(2, Item.get(Block.AIR));
                            }
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerCheatEvent(PlayerInvalidMoveEvent event){
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(player != null){
            if(player.isOp()){ return; }
            Room room = Room.getRoom(player);
            if(room != null){
                player.sendMessage(TextFormat.RED + "游戏内玩家不可执行指令！");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Level fromLevel = event.getFrom().getLevel();
        Level toLevel = event.getTo().getLevel();
        if (player == null || fromLevel == null || toLevel == null) {
            return;
        }
        Room room = Room.getRoom(player);
        if(room != null) {
            if (!fromLevel.equals(toLevel)) {
                List<Level> arenas = new LinkedList<>();
                arenas.add(room.getWaitSpawn().getLevel());
                arenas.add(room.getStartSpawn().getLevel());
                if (arenas.contains(fromLevel) && arenas.contains(toLevel)) {
                    return;
                }else if (arenas.contains(fromLevel) && arenas.contains(toLevel)) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && Room.getRoom(event.getPlayer()) != null) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void FlyEvent(PlayerToggleFlightEvent event){
        if(Room.getRoom(event.getPlayer()) == null){ return; }
        if(!event.getPlayer().isOp()){
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
}
