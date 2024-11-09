package gameapi.entity;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author glorydark
 */
public class EntityItemDisplay extends EntityItem {

    private int spinRotValuePerTick = -1;

    public EntityItemDisplay(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        int tickDiff = currentTick - this.lastUpdate;
        if (this.spinRotValuePerTick != -1) {
            this.setYaw(this.getYaw() >= 360? this.spinRotValuePerTick * tickDiff: this.spinRotValuePerTick + this.getYaw());
        }
        return true;
    }

    public void setSpinRotValuePerTick(int spinRotValuePerTick) {
        this.spinRotValuePerTick = spinRotValuePerTick;
    }

    public int getSpinRotValuePerTick() {
        return spinRotValuePerTick;
    }
}
