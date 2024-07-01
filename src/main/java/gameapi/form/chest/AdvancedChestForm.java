package gameapi.form.chest;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedChestForm extends AdvancedFakeBlockContainerFormBase {

    public AdvancedChestForm(String title) {
        super(BlockEntity.CHEST, Block.CHEST, title);
    }

    public AdvancedChestForm onClick(BiConsumer<Player, Item> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedChestForm onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    public AdvancedChestForm item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.getInventory().put(slot, item);
        this.getResponseMap().put(slot, slotItem.getResponse());
        return this;
    }
}
