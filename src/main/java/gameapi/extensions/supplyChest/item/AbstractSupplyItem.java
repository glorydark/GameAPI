package gameapi.extensions.supplyChest.item;

import cn.nukkit.item.Item;
import gameapi.annotation.Experimental;

/**
 * @author glorydark
 */
@Experimental
public abstract class AbstractSupplyItem {

    protected final Item item;

    protected AbstractSupplyItem(Item item) {
        this.item = item;
    }

    public Item select() {
        return null;
    }

    public Item getItem() {
        return item;
    }
}
