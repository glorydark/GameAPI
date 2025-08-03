package gameapi.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextEntity extends Entity {

    private final Map<String, Object> extraProperties = new LinkedHashMap<>();

    private int maxShowDistance = -1;

    public TextEntity(FullChunk chunk, String text, CompoundTag nbt) {
        super(chunk, nbt);
        this.setNameTag(text);
    }

    protected void initEntity() {
        super.initEntity();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
        this.setScale(0f);
        this.setCanBeSavedWithChunk(false);
    }

    public int getNetworkId() {
        return EntityArmorStand.NETWORK_ID;
    }

    public void onAsyncUpdate(int currentTick) {
        if (this.isClosed()) {
            return;
        }
        /*
        if (Arrays.stream(this.level.getEntities()).noneMatch(entity -> entity == this)) {
            this.getLevel().addEntity(this);
        }
         */
        for (Player player : new ArrayList<>(this.getLevel().getPlayers().values())) {
            if (this.getViewers().containsKey(player.getLoaderId())) {
                if (!player.isOnline() || player.getLevel() != this.getLevel() || (this.maxShowDistance != -1 && player.distance(this) > this.maxShowDistance)) {
                    this.despawnFrom(player);
                }
            } else {
                if (player.getLevel() == this.getLevel() && (this.maxShowDistance == -1 || player.distance(this) <= this.maxShowDistance)) {
                    this.spawnTo(player);
                }
            }
        }
    }

    public boolean respawn() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public void saveNBT() {

    }

    @Override
    public boolean canBeSavedWithChunk() {
        return false;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
    }

    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    public void setMaxShowDistance(int maxShowDistance) {
        this.maxShowDistance = maxShowDistance;
    }

    public int getMaxShowDistance() {
        return maxShowDistance;
    }
}