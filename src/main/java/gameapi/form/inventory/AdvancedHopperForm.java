package gameapi.form.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.form.AdvancedFakeBlockContainerFormBaseImpl;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.response.ChestResponse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedHopperForm extends AdvancedFakeBlockContainerFormBaseImpl {

    public AdvancedHopperForm(String title) {
        this(title, false);
    }

    public AdvancedHopperForm(String title, boolean movable) {
        super(title, AdvancedChestFormType.HOPPER, movable);
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
