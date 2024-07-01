package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.form.response.ChestResponse;
import gameapi.room.items.RoomItemBase;

import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementSlotItem {

    protected Item item;

    private BiConsumer<Player, ChestResponse> response;

    public ResponsiveElementSlotItem(int id) {
        this(id, 0);
    }

    public ResponsiveElementSlotItem(int id, int meta) {
        this(id, meta, 1);
    }

    public ResponsiveElementSlotItem(int id, int meta, int count) {
        this.item = Item.get(id, meta, count);
    }

    public ResponsiveElementSlotItem(String itemString) {
        this.item = Item.fromString(itemString);
    }

    public ResponsiveElementSlotItem(Item item) {
        this.item = item;
    }


    public ResponsiveElementSlotItem onRespond(BiConsumer<Player, ChestResponse> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, ChestResponse> getResponse() {
        return response;
    }

    // basic parts
    public ResponsiveElementSlotItem customName(String name) {
        this.getItem().setCustomName(name);
        return this;
    }

    public ResponsiveElementSlotItem lore(String... lines) {
        this.getItem().setLore(lines);
        return this;
    }

    public ResponsiveElementSlotItem lockType(RoomItemBase.ItemLockType lockType) {
        CompoundTag tag = this.getItem().getNamedTag();
        if (lockType.ordinal() > 0 && lockType.ordinal() < 3) {
            tag.putByte("minecraft:item_lock", lockType.ordinal());
        }
        this.getItem().setCompoundTag(tag);
        return this;
    }

    public ResponsiveElementSlotItem enchant(Enchantment... enchantment) {
        this.getItem().addEnchantment(enchantment);
        return this;
    }

    public Item getItem() {
        return item;
    }
}
