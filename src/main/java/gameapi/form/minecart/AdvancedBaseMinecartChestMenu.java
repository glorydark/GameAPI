package gameapi.form.minecart;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.annotation.Future;
import gameapi.form.AdvancedChestFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.response.ChestResponse;
import gameapi.listener.AdvancedFormListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
@Future
public class AdvancedBaseMinecartChestMenu extends AdvancedChestFormBase {

    protected String title;
    protected CustomEntityMinecartChest entity = null;
    protected BiConsumer<Player, Item> clickBiConsumer = null;
    protected Consumer<Player> closeConsumer = null;

    public AdvancedBaseMinecartChestMenu(String title) {
        this.title = title;
    }

    public AdvancedBaseMinecartChestMenu item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.getInventory().put(slot, item);
        this.getResponseMap().put(slot, slotItem.getResponse());
        if (this.getEntity() != null) {
            this.getEntity().getInventory().setItem(slot, item);
        }
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AdvancedBaseMinecartChestMenu onClick(BiConsumer<Player, Item> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedBaseMinecartChestMenu onClose(Consumer<Player> consumer) {
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
        this.entity = chest;
        AdvancedFormListener.showToPlayer(player, this);
    }

    @Override
    protected void closeProcess(Player player) {
        this.entity.getInventory().clearAll();
        this.entity.despawnFrom(player);
        this.entity.close();
        AdvancedFormListener.removeChestMenuCache(player);
    }

    public CustomEntityMinecartChest getEntity() {
        return entity;
    }

    public BiConsumer<Player, Item> getClickBiConsumer() {
        return clickBiConsumer;
    }

    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }
}
