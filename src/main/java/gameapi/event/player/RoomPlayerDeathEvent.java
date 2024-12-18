package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;
import gameapi.utils.EntityDamageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomPlayerDeathEvent extends RoomPlayerEvent implements Cancellable {

    protected EntityDamageSource lastDamageByPlayerSource;

    protected EntityDamageSource lastDamageByEntitySource;

    protected List<EntityDamageSource> assistingDamageSource;

    protected EntityDamageEvent.DamageCause cause;

    protected boolean keepInventory = true;

    protected boolean keepExp = true;

    protected boolean sendTitle;

    protected String title = "";

    protected String subtitle = "";

    protected boolean respawn = true;

    public RoomPlayerDeathEvent(Room room, Player player, boolean sendTitle, EntityDamageEvent.DamageCause cause) {
        super(room, player);
        //导入的伤害来源
        this.lastDamageByPlayerSource = room.getLastEntityDamageByPlayerSource(player).orElse(null);
        this.lastDamageByEntitySource = room.getLastEntityDamageByEntitySource(player).orElse(null);
        List<EntityDamageSource> entityDamageSources = new ArrayList<>(room.getLastEntityReceiveDamageSource().getOrDefault(player, new ArrayList<>()));
        entityDamageSources.remove(this.lastDamageByEntitySource);
        entityDamageSources.remove(this.lastDamageByPlayerSource);
        this.cause = cause;
        this.sendTitle = sendTitle;
        this.assistingDamageSource = entityDamageSources;
        room.getLastEntityReceiveDamageSource().remove(player);
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

    public boolean isRespawn() {
        return respawn;
    }

    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    public List<EntityDamageSource> getAssistingDamageSource() {
        return assistingDamageSource;
    }

    public Optional<EntityDamageSource> getLastDamageByEntitySource() {
        return Optional.ofNullable(lastDamageByEntitySource);
    }

    public Optional<EntityDamageSource> getLastDamageByPlayerSource() {
        return Optional.ofNullable(lastDamageByPlayerSource);
    }
}
