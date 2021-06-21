package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import gameapi.MainClass;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class PlayerEvent implements Listener {
    @EventHandler
    public void Exit(PlayerQuitEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            for (Player p : room.players) {
                p.sendMessage("玩家" + event.getPlayer().getName() + "退出了本房间");
            }
            room.players.remove(event.getPlayer());
            if(room.players.size() < 1){
                room.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if (!room.roomRule.canBreakBlocks.contains(event.getBlock().getId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if (!room.roomRule.canPlaceBlocks.contains(event.getBlock().getId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        for (Room room:MainClass.RoomHashMap){
            if(room.players.contains(event.getPlayer())) {
                if (room.roomRule.noDropItem) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDeathEvent(PlayerDeathEvent event){
        if(event.getEntity() instanceof Player){
            Room room = Room.getRoom(event.getEntity());
            if(room != null) {
                if (room.roomRule.allowRespawn) {
                    Server.getInstance().getScheduler().scheduleDelayedTask(MainClass.plugin, () -> {event.getEntity().respawnToAll(); event.getEntity().teleportImmediate(room.startSpawn.getLocation());}, room.roomRule.spawnCoolDown);
                }else{
                    event.getEntity().setGamemode(3);
                }
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        if(Room.getRoom(event.getPlayer()) != null) {
            for (Room room:MainClass.RoomHashMap){
                for(Player player: room.players){
                    if(player == event.getPlayer()){
                        if(room.roomStatus == RoomStatus.ROOM_STATUS_GameReadyStart && room.roomRule.startNoWalk){
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void PlayerDamageEvent(EntityDamageByEntityEvent event){
        switch (event.getCause()){
            case ENTITY_ATTACK:
                if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                    if (Room.getRoom((Player) event.getDamager()) != null) { //攻击者在房间
                        if (!Room.getRoom((Player) event.getDamager()).roomRule.allowTeamDamage) {
                            for (String s : Room.getRoom((Player) event.getDamager()).teamCache.keySet()) {
                                if (Room.getRoom((Player) event.getDamager()).teamCache.get(s).contains(event.getEntity())) { //受害者与其在同队
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                        if (!Room.getRoom((Player) event.getDamager()).roomRule.allowDamagePlayer) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                break;
            case FALL:
                if (Room.getRoom((Player) event.getEntity()) != null) { //攻击者在房间
                    if(Room.getRoom((Player) event.getEntity()).roomRule.noDropDamage){
                        event.setCancelled(true);
                    }
                }
                break;
        }
    }

    @EventHandler
    public void PlayerCheatEvent(PlayerInvalidMoveEvent event){
        event.setCancelled(true);
    }
}
