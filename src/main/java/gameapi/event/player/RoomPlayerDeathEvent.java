package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.listener.BaseEventListener;
import gameapi.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPlayerDeathEvent extends RoomPlayerEvent implements Cancellable {

    private final BaseEventListener.DamageSource lastDamageSource;

    private final List<BaseEventListener.DamageSource> assistingDamageSource;

    private final EntityDamageEvent.DamageCause cause;

    public RoomPlayerDeathEvent(Room room, Player player, EntityDamageEvent.DamageCause cause) {
        super(room, player);
        //导入的伤害来源
        List<BaseEventListener.DamageSource> inputDamageSources = BaseEventListener.damageSources.getOrDefault(player.getName(), new ArrayList<>());
        //利用lambda进行filter，筛掉不合格的伤害来源。
        List<BaseEventListener.DamageSource> sortedSources = inputDamageSources.stream().filter(damageSource -> System.currentTimeMillis() - damageSource.getMilliseconds() <= 10000).collect(Collectors.toList());
        sortedSources = sortedSources.stream().sorted((o1, o2) -> (int) (o1.getMilliseconds() - o2.getMilliseconds())).collect(Collectors.toList());
        if (sortedSources.size() > 0) {
            this.lastDamageSource = sortedSources.get(sortedSources.size() - 1);
        } else {
            this.lastDamageSource = new BaseEventListener.DamageSource("", 0);
        }
        sortedSources.remove(lastDamageSource);
        this.assistingDamageSource = sortedSources;
        this.cause = cause;
    }

    public Player getLastDamageSource() {
        if (!lastDamageSource.getDamager().equals("")) {
            if (System.currentTimeMillis() - lastDamageSource.getMilliseconds() <= 5000) {
                //When the difference between now and last damage time is over 5 seconds, GameAPI will regard this as an outdated damage source;
                return Server.getInstance().getPlayer(lastDamageSource.getDamager());
            }
        }
        return null;
    }

    public List<BaseEventListener.DamageSource> getAssistedSource() {
        return assistingDamageSource;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
