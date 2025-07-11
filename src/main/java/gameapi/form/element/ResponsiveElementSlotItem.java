package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.form.response.BlockInventoryResponse;
import gameapi.room.items.RoomItemBase;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class ResponsiveElementSlotItem {

    protected Item item;

    private BiConsumer<Player, BlockInventoryResponse> onClickResponse;

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
        this(Item.fromString(itemString));
    }

    public ResponsiveElementSlotItem(Item item) {
        this.item = item;
    }


    public ResponsiveElementSlotItem onRespond(BiConsumer<Player, BlockInventoryResponse> response) {
        this.onClickResponse = response;
        return this;
    }

    public BiConsumer<Player, BlockInventoryResponse> getOnClickResponse() {
        return onClickResponse;
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

    public ResponsiveElementSlotItem tag(Consumer<CompoundTag> tagConsumer) {
        CompoundTag current = this.item.getNamedTag() == null? new CompoundTag() : this.item.getNamedTag();
        tagConsumer.accept(current);
        this.item.setCompoundTag(current);
        return this;
    }

    public Item getItem() {
        return item;
    }
}
