package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;

import java.util.ArrayList;

public class TextEntity extends Entity {

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
    }

    public int getNetworkId() {
        return 64;
    }

    public boolean onUpdate(int currentTick) {
        if (this.isClosed()) {
            return false;
        }
        if (currentTick % GameAPI.TEXT_ENTITY_UPDATE_TICK_INTERVAL != 0) {
            return super.onUpdate(currentTick);
        }
        for (Player player : new ArrayList<>(this.getViewers().values())) {
            if (!player.isOnline() || player.getLevel() != this.getLevel()) {
                this.despawnFrom(player);
            }
        }
        return super.onUpdate(currentTick);
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
}