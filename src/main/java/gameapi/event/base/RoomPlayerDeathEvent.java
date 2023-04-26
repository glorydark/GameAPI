package gameapi.event.base;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.listener.PlayerEventListener;
import gameapi.room.Room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPlayerDeathEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final Player player;

    private final PlayerEventListener.DamageSource lastDamageSource;

    private final List<PlayerEventListener.DamageSource> assistingDamageSource;

    private EntityDamageEvent.DamageCause cause;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPlayerDeathEvent(Room room, Player player, EntityDamageEvent.DamageCause cause){
        this.room = room;
        this.player = player;
        //导入的伤害来源
        List<PlayerEventListener.DamageSource> inputDamageSources = PlayerEventListener.damageSources.getOrDefault(player.getName(), new ArrayList<>());
        //利用lambda进行filter，筛掉不合格的伤害来源。
        List<PlayerEventListener.DamageSource> sortedSources = inputDamageSources.stream().filter(damageSource -> System.currentTimeMillis() - damageSource.getMilliseconds() <= 10000).collect(Collectors.toList());
        sortedSources = sortedSources.stream().sorted((o1, o2) -> (int) (o1.getMilliseconds() - o2.getMilliseconds())).collect(Collectors.toList());
        if(sortedSources.size() > 0){
            this.lastDamageSource = sortedSources.get(sortedSources.size()-1);
        }else{
            this.lastDamageSource = new PlayerEventListener.DamageSource("", 0);
        }
        sortedSources.remove(lastDamageSource);
        this.assistingDamageSource = sortedSources;
        this.cause = cause;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }

    public Player getLastDamageSource() {
        if(!lastDamageSource.getDamager().equals("")){
            if(System.currentTimeMillis() - lastDamageSource.getMilliseconds() <= 5000){
                //When the difference between now and last damage time is over 5 seconds, GameAPI will regard this as an outdated damage source;
                return Server.getInstance().getPlayer(lastDamageSource.getDamager());
            }
        }
        return null;
    }

    public List<PlayerEventListener.DamageSource> getAssistedSource(){
        return assistingDamageSource;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
