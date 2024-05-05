package gameapi.room.items;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.event.player.RoomPlayerItemHeldEvent;
import gameapi.tools.ItemTools;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class RoomItemBase {

    protected Item item;

    protected String identifier;

    protected Consumer<RoomPlayerInteractEvent> interactConsumer = null;

    protected Consumer<RoomPlayerItemHeldEvent> itemHeldConsumer = null;

    public RoomItemBase(String identifier, String name, Item item) {
        this(identifier, name, item, ItemLockType.NONE);
    }

    public RoomItemBase(String identifier, String name, Item item, ItemLockType lockType) {
        this.item = item;
        this.identifier = identifier;
        this.item.setCustomName(name);
        CompoundTag tag = this.item.getNamedTag();
        tag.putString("room_item", identifier);
        if (lockType.ordinal() > 0 && lockType.ordinal() < 3) {
            tag.putByte("minecraft:item_lock", lockType.ordinal());
        }
        this.item.setCompoundTag(tag);
    }

    public RoomItemBase(String identifier, String name, String itemString) {
        this(identifier, name, Item.fromString(itemString));
    }

    public RoomItemBase(String identifier, String name, Map<String, Object> itemMap) {
        this(identifier, name, ItemTools.parseItemFromMap(itemMap));
    }

    public static String getRoomItemIdentifier(Item item) {
        if (!item.hasCompoundTag()) {
            return "";
        }
        return item.getNamedTag().getString("room_item");
    }

    public Item getItem() {
        return item.clone();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void executeInteract(RoomPlayerInteractEvent event) {
        if (interactConsumer != null) {
            interactConsumer.accept(event);
        }
    }

    public void executeHeldItem(RoomPlayerItemHeldEvent event) {
        if (itemHeldConsumer != null) {
            itemHeldConsumer.accept(event);
        }
    }

    public RoomItemBase onInteract(Consumer<RoomPlayerInteractEvent> interactConsumer) {
        this.interactConsumer = interactConsumer;
        return this;
    }

    public RoomItemBase onItemHeld(Consumer<RoomPlayerItemHeldEvent> itemHeldConsumer) {
        this.itemHeldConsumer = itemHeldConsumer;
        return this;
    }

    public enum ItemLockType {
        NONE,
        LOCK_IN_INVENTORY,
        LOCK_IN_SLOT
    }
}
