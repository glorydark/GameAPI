package gameapi.extensions.supplyChest.item;

import cn.nukkit.item.Item;

/**
 * @author glorydark
 */
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
