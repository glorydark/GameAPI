package gameapi.form.chest;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import gameapi.form.AdvancedChestFormBase;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.response.ChestResponse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedHopperForm extends AdvancedFakeBlockContainerFormBase {

    public AdvancedHopperForm(String title) {
        super(BlockEntity.HOPPER, Block.HOPPER_BLOCK, title, InventoryType.HOPPER);
    }

    public AdvancedHopperForm onClick(BiConsumer<Player, ChestResponse> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedHopperForm onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    public AdvancedHopperForm item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.getInventory().put(slot, item);
        this.getResponseMap().put(slot, slotItem.getResponse());
        return this;
    }
}
