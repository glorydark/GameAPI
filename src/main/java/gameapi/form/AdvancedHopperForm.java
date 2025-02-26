package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.inventory.BlockFakeInventoryType;
import gameapi.form.inventory.block.AdvancedBlockFakeBlockInventoryImpl;
import gameapi.form.response.BlockInventoryResponse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedHopperForm extends AdvancedBlockFakeBlockInventoryImpl {

    public AdvancedHopperForm(String title) {
        super(title, BlockFakeInventoryType.HOPPER);
    }

    public AdvancedHopperForm onClick(BiConsumer<Player, BlockInventoryResponse> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedHopperForm onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    public AdvancedHopperForm item(int slot, ResponsiveElementSlotItem slotItem) {
        this.addItem(slot, slotItem);
        return this;
    }

    public void addItem(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.addItemToSlot(slot, item);
        this.getResponseMap().put(slot, slotItem.getOnClickResponse());
    }
}
