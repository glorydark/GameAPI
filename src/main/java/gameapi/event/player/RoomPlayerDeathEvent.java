package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.listener.BaseEventListener;
import gameapi.room.Room;
import gameapi.utils.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class RoomPlayerDeathEvent extends RoomPlayerEvent implements Cancellable {

    protected DamageSource lastDamageSource;

    protected List<DamageSource> assistingDamageSource;

    protected EntityDamageEvent.DamageCause cause;

    protected boolean keepInventory = true;

    protected boolean keepExp = true;

    protected boolean sendTitle;

    protected String title = "";

    protected String subtitle = "";

    public RoomPlayerDeathEvent(Room room, Player player, boolean sendTitle, EntityDamageEvent.DamageCause cause) {
        super(room, player);
        //导入的伤害来源
        List<DamageSource> sources = BaseEventListener.damageSources.getOrDefault(player.getName(), new ArrayList<>());
        if (!sources.isEmpty()) {
            this.lastDamageSource = sources.get(sources.size() - 1);
            sources.remove(lastDamageSource);
        } else {
            this.lastDamageSource = new DamageSource("", 0);
        }
        this.assistingDamageSource = sources;
        this.cause = cause;
        this.sendTitle = sendTitle;
    }

    public Player getLastDamageSource() {
        return Server.getInstance().getPlayer(lastDamageSource.getDamager());
    }

    public List<DamageSource> getAssistedSource() {
        return assistingDamageSource;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public boolean isKeepExp() {
        return keepExp;
    }

    public void setKeepExp(boolean keepExp) {
        this.keepExp = keepExp;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isSendTitle() {
        return sendTitle;
    }

    public void setSendTitle(boolean sendTitle) {
        this.sendTitle = sendTitle;
    }
}
