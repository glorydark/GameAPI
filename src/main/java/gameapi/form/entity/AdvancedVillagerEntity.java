package gameapi.form.entity;

import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.listener.AdvancedFormListener;

/**
 * @author glorydark
 */
public class AdvancedVillagerEntity extends EntityVillagerV2 {

    public AdvancedVillagerEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.namedTag.putBoolean(AdvancedFormListener.VILLAGER_ENTITY_TAG, true);
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        return false;
    }
}
