package gameapi.form.chest;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.response.ChestResponse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedChestForm extends AdvancedFakeBlockContainerFormBase {

    public AdvancedChestForm(String title) {
        this(title, false);
    }

    public AdvancedChestForm(String title, boolean movable) {
        super(title, AdvancedChestFormType.CHEST, movable);
    }

    public AdvancedChestForm onClick(BiConsumer<Player, ChestResponse> consumer) {
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
