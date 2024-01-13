package gameapi.room.items;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.event.player.RoomPlayerItemHeldEvent;
import gameapi.utils.SmartTools;

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
        this.item.getNamedTag().putString("room_item", getIdentifier());
        if (lockType.ordinal() > 0 && lockType.ordinal() < 3) {
            this.item.getNamedTag().putByte("minecraft:item_lock", lockType.ordinal());
        }
    }

    public RoomItemBase(String identifier, String name, String itemString) {
        this(identifier, name, Item.fromString(itemString));
    }

    public RoomItemBase(String identifier, String name, Map<String, Object> itemMap) {
        this(identifier, name, SmartTools.parseItemFromMap(itemMap));
    }

    public static String getRoomItemIdentifier(Item item) {
        if (!item.hasCompoundTag()) {
            return "";
        }
        return item.getNamedTag().getString("room_item");
    }

    public Item getItem() {
        return item;
    }

    public Item toRoomItem() {
        Item item = getItem().clone();
        return item;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean onInteract(RoomPlayerInteractEvent event) {
        return true;
    }

    public boolean onPlaceBlock(RoomBlockPlaceEvent event) {
        return true;
    }

    public boolean onHeldItem(RoomPlayerItemHeldEvent event) {
        return true;
    }

    public enum ItemLockType {
        NONE,
        LOCK_IN_INVENTORY,
        LOCK_IN_SLOT
    }
}
