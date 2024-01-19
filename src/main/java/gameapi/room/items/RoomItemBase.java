package gameapi.room.items;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.event.player.RoomPlayerItemHeldEvent;
import gameapi.toolkit.InventoryTools;

import java.util.Map;

/**
 * @author glorydark
 */
public abstract class RoomItemBase {

    protected Item item;

    protected String identifier;

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
        this(identifier, name, InventoryTools.parseItemFromMap(itemMap));
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

    public void onInteract(RoomPlayerInteractEvent event) {
    }

    public void onHeldItem(RoomPlayerItemHeldEvent event) {
    }

    public enum ItemLockType {
        NONE,
        LOCK_IN_INVENTORY,
        LOCK_IN_SLOT
    }
}
