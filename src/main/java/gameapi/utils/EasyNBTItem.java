package gameapi.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.room.items.RoomItemBase;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class EasyNBTItem {

    private final String identifier;

    private final int id;

    private final int meta;

    private String customName = "";

    protected CompoundTag compoundTag = new CompoundTag();

    protected String[] lore = null;

    public EasyNBTItem(String identifier) {
        this(identifier, 0);
    }

    public EasyNBTItem(String identifier, int meta) {
        this.identifier = identifier;
        this.id = 255;
        this.meta = meta;
    }

    public EasyNBTItem(int id) {
        this(id, 0);
    }

    public EasyNBTItem(int id, int meta) {
        this.identifier = "";
        this.id = id;
        this.meta = meta;
    }

    public EasyNBTItem lore(String... strings) {
        this.lore = strings;
        return this;
    }

    public EasyNBTItem customName(String customName) {
        this.customName = customName;
        return this;
    }

    public EasyNBTItem listTag(String key, ListTag<?> value) {
        this.compoundTag.putList(key, value);
        return this;
    }

    public EasyNBTItem doubleTag(String key, double value) {
        this.compoundTag.putDouble(key, value);
        return this;
    }

    public EasyNBTItem floatTag(String key, float value) {
        this.compoundTag.putFloat(key, value);
        return this;
    }

    public EasyNBTItem intTag(String key, int value) {
        this.compoundTag.putInt(key, value);
        return this;
    }

    public EasyNBTItem boolTag(String key, boolean value) {
        this.compoundTag.putBoolean(key, value);
        return this;
    }

    public EasyNBTItem stringTag(String key, String value) {
        this.compoundTag.putString(key, value);
        return this;
    }

    public EasyNBTItem byteTag(String key, byte value) {
        this.compoundTag.putByte(key, value);
        return this;
    }

    public EasyNBTItem lockType(RoomItemBase.ItemLockType lockType) {
        if (lockType == RoomItemBase.ItemLockType.NONE) {
            return this;
        }
        this.compoundTag.putByte("minecraft:item_lock", lockType.ordinal());
        return this;
    }

    public EasyNBTItem unbreakable(boolean unbreakable) {
        if (unbreakable) {
            this.compoundTag.putBoolean("Unbreakable", unbreakable);
        }
        return this;
    }

    public Item toItem() {
        Item item;
        if (this.id == 255) {
            item = Item.fromString(this.identifier);
            if (this.meta != 0) {
                item.setDamage(this.meta);
            }
        } else {
            item = Item.get(this.id, this.meta);
        }
        if (!this.compoundTag.isEmpty()) {
            item.setCompoundTag(this.compoundTag);
        }
        if (!this.customName.isEmpty()) {
            item.setCustomName(this.customName);
        }
        if (this.lore != null) {
            item.setLore(this.lore);
        }
        return item;
    }
}
