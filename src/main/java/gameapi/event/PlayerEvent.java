package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.event.player.*;
import gameapi.MainClass;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.Sound;

public class PlayerEvent implements Listener {
    @EventHandler
    public void Join(PlayerLocallyInitializedEvent event){
        if(event.getPlayer() != null) {
            if (Inventory.getPlayerBagConfig(event.getPlayer()) != null) {
                Sound.playResourcePackOggMusic(event.getPlayer(),"winning.ogg");
                Sound.playResourcePackOggMusic(event.getPlayer(),"winning");
                Inventory.loadBag(event.getPlayer());
                Server.getInstance().getLogger().info("检测到玩家"+event.getPlayer().getName()+"背包上次未能返还，已经返还！");
            }
        }
    }

    @EventHandler
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
                } else {
                    room.addBreakBlock(event.getBlock());
                }
            }else {
                room.addBreakBlock(event.getBlock());
            }
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
                }else{
                    room.addPlaceBlock(event.getBlock());
                }
            }else{
                room.addPlaceBlock(event.getBlock());
            }
        }
    }

    @EventHandler
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

    @EventHandler
    public void ExplodeEvent(EntityExplodeEvent event){
        Room room = Room.getRoom(event.getPosition().level);
        if(room != null){
            if(room.getRoomRule().antiExplosion){
                event.getEntity().kill();
                event.setCancelled(true);
            }else {
                room.addBreakBlocks(event.getBlockList());
            }
        }
    }

    @EventHandler
    public void ExplodePrimeEvent(ExplosionPrimeEvent event){
        Room room = Room.getRoom(event.getEntity().getLevel());
        if(room != null) {
            if (room.getRoomRule().antiExplosion) {
                event.getEntity().kill();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
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

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        if(entity instanceof Player){
            event.setCancelled(true);
        }
    }


    @EventHandler
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

    @EventHandler
    public void PlayerCheatEvent(PlayerInvalidMoveEvent event){
        event.setCancelled(true);
    }
}
