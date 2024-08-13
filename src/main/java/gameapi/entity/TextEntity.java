package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

public class TextEntity extends Entity {

    public TextEntity(FullChunk chunk, Position position, String text, CompoundTag nbt) {
        super(chunk, nbt);
        this.setPosition(position);
        this.setNameTag(text);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
    }

    protected void initEntity() {
        super.initEntity();
        this.getDataProperties().putLong(0, 65536L);
    }

    public int getNetworkId() {
        return 64;
    }

    public boolean onUpdate(int currentTick) {
        for (Player player : new ArrayList<>(this.getViewers().values())) {
            if (!player.isOnline() && player.getLevel() != this.getLevel()) {
                this.despawnFrom(player);
            }
        }
        return super.onUpdate(currentTick);
    }
}