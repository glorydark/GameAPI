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
public class AdvancedChestForm extends AdvancedBlockFakeBlockInventoryImpl {

    public AdvancedChestForm(String title) {
        super(title, BlockFakeInventoryType.CHEST);
    }

    public AdvancedChestForm onClick(BiConsumer<Player, BlockInventoryResponse> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedChestForm onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    public AdvancedChestForm item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.addItemToSlot(slot, item);
        this.getResponseMap().put(slot, slotItem.getOnClickResponse());
        return this;
    }
}
