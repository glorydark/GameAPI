package gameapi.form.minecart;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.annotation.Future;
import gameapi.form.AdvancedChestFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.response.ChestResponse;
import gameapi.listener.AdvancedFormListener;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
@Future
public class AdvancedMinecartChestMenu extends AdvancedChestFormBase {

    protected LinkedHashMap<Player, EntityMinecartChest> entityMap = null;
    protected BiConsumer<Player, Item> clickBiConsumer = null;
    protected Consumer<Player> closeConsumer = null;

    public AdvancedMinecartChestMenu(String title) {
        super(title, InventoryType.CHEST);
    }

    public AdvancedMinecartChestMenu item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.getInventory().put(slot, item);
        this.getResponseMap().put(slot, slotItem.getResponse());
        return this;
    }

    public AdvancedMinecartChestMenu onClick(BiConsumer<Player, Item> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedMinecartChestMenu onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    @Override
    public void dealResponse(Player player, ChestResponse chestResponse) {
        if (chestResponse == null) {
            Consumer<Player> consumer = this.getCloseConsumer();
            if (consumer != null) {
                consumer.accept(player);
            }
        } else {
            Item item = chestResponse.getItem();
            BiConsumer<Player, Item> consumer = this.getResponseMap().get(chestResponse.getSlot());
            if (consumer != null) {
                consumer.accept(player, item);
            }

            consumer = this.getClickBiConsumer();
            if (consumer != null) {
                consumer.accept(player, item);
            }
        }
    }

    public void close(Player player) {
        this.closeProcess(player);
    }

    @Override
    public void showToPlayer(Player player) {
        CustomEntityMinecartChest chest = new CustomEntityMinecartChest(player.getChunk(), EntityMinecartChest.getDefaultNBT(player.getPosition()), player);
        chest.namedTag.putList(new ListTag("Items"));
        chest.namedTag.putByte("Slot", 27);
        chest.namedTag.putBoolean("Invulnerable", true);
        chest.namedTag.putBoolean("CustomDisplayTile", false);
        chest.namedTag.putBoolean(AdvancedFormListener.FORM_ENTITY_TAG, true);
        chest.getInventory().setContents(this.getInventory());
        chest.setNameTag(this.getTitle());
        chest.setNameTagVisible(false);
        chest.setNameTagAlwaysVisible(false);
        chest.setImmobile(true);
        chest.spawnTo(player);
        player.addWindow(chest.getInventory());
        entityMap.put(player, chest);
        AdvancedFormListener.showToPlayer(player, this);
    }

    @Override
    protected void closeProcess(Player player) {
        EntityMinecartChest entityMinecartChest = entityMap.get(player);
        if (entityMinecartChest != null) {
            entityMinecartChest.getInventory().clearAll();
            entityMinecartChest.despawnFrom(player);
            entityMinecartChest.close();
            AdvancedFormListener.removeChestMenuCache(player);
        }
    }

    public LinkedHashMap<Player, EntityMinecartChest> getEntityMap() {
        return entityMap;
    }

    public BiConsumer<Player, Item> getClickBiConsumer() {
        return clickBiConsumer;
    }

    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }
}
