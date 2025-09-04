package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import gameapi.event.Cancellable;
import gameapi.room.Room;
import gameapi.utils.EntityDamageSource;

import java.util.*;

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

    protected boolean respawn;
    protected boolean teleport = true;
    protected final Location deathLocation;

    protected boolean keepDamageSource = false;

    public RoomPlayerDeathEvent(Room room, Player player, boolean sendTitle, EntityDamageEvent.DamageCause cause) {
        super(room, player);
        this.deathLocation = player.getLocation();
        this.respawn = room.getRoomRule().isAllowRespawn();
        //导入的伤害来源
        this.lastDamageByPlayerSource = room.getLastEntityDamageByPlayerSource(player).orElse(null);
        this.lastDamageByEntitySource = room.getLastEntityDamageByEntitySource(player).orElse(null);
        // 创建防御性副本并过滤null值
        List<EntityDamageSource> damageSources = Optional.of(new HashMap<>(room.getLastEntityReceiveDamageSource()))
                .map(map -> map.get(player))
                .orElseGet(ArrayList::new);
        List<EntityDamageSource> filteredSources = new ArrayList<>(damageSources
                .stream()
                .filter(Objects::nonNull)
                .toList());
        filteredSources.removeIf(source -> source == this.lastDamageByEntitySource || source == this.lastDamageByPlayerSource);
        this.cause = cause;
        this.sendTitle = sendTitle;
        this.assistingDamageSource = filteredSources;
        if (!this.keepDamageSource) {
            room.getLastEntityReceiveDamageSource().remove(player);
        }
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

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
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

    public Location getDeathLocation() {
        return deathLocation;
    }

    public boolean isKeepDamageSource() {
        return keepDamageSource;
    }

    public void setKeepDamageSource(boolean keepDamageSource) {
        this.keepDamageSource = keepDamageSource;
    }
}
