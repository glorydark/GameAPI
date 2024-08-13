package gameapi.room.items;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.room.Room;
import gameapi.tools.ItemTools;

import java.util.Map;

/**
 * @author glorydark
 */
public abstract class RoomItemBase {

    protected Item item;

    protected String identifier;

    public static final String KEY_IDENTIFIER = "room_item";

    public RoomItemBase(String identifier, String name, Item item) {
        this(identifier, name, item, ItemLockType.NONE);
    }

    public RoomItemBase(String identifier, String name, Item item, ItemLockType lockType) {
        this.item = item;
        this.identifier = identifier;
        this.item.setCustomName(name);
        this.item.getNamedTag().putString(KEY_IDENTIFIER, this.identifier);
        if (lockType.ordinal() > 0 && lockType.ordinal() < 3) {
            this.item.getNamedTag().putByte("minecraft:item_lock", lockType.ordinal());
        }
        this.item.setCompoundTag(this.item.getNamedTag());
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
        return item.getNamedTag().getString(KEY_IDENTIFIER);
    }

    public Item toItem() {
        return this.item.clone();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void onInteract(Room room, Player player, Item item) {

    }

    public void onBlockPlace(Room room, Player player, Item item) {

    }

    public void onItemHeld(Room room, Player player, Item item) {

    }

    public enum ItemLockType {
        NONE,
        LOCK_IN_SLOT,
        LOCK_IN_INVENTORY
    }

    public void executeCoolDown(Player player, Item heldItem, long delay) {
        heldItem.getNamedTag().putLong("next_use_millis", System.currentTimeMillis() + delay);
        heldItem.setCompoundTag(heldItem.getNamedTag());
        player.getInventory().setItemInHand(heldItem);
    }
}
