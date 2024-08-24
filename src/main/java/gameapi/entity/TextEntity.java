package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;

import java.util.ArrayList;

public class TextEntity extends Entity {

    public TextEntity(FullChunk chunk, Position position, String text, CompoundTag nbt) {
        super(chunk, nbt);
        this.setPosition(position);
        this.setNameTag(text);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
    }

    protected void initEntity() {
        super.initEntity();
    }

    public int getNetworkId() {
        return 64;
    }

    public boolean onUpdate(int currentTick) {
        if (currentTick % GameAPI.TEXT_ENTITY_UPDATE_TICK_INTERVAL != 0) {
            return super.onUpdate(currentTick);
        }
        for (Player player : new ArrayList<>(this.getViewers().values())) {
            if (!player.isOnline() || player.getLevel() != this.getLevel()) {
                this.despawnFrom(player);
            }
        }
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean canBeSavedWithChunk() {
        return false;
    }
}