package gameapi.fakeInventory.minecart;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author glorydark
 */
public class AdvancedEntityMinecartChest extends EntityMinecartChest {

    private final Player owner;

    public AdvancedEntityMinecartChest(FullChunk chunk, CompoundTag nbt, Player owner) {
        super(chunk, nbt);
        this.owner = owner;
    }

    @Override
    public void dropItem() {
        // no-do
    }

    public Player getOwner() {
        return owner;
    }
}
