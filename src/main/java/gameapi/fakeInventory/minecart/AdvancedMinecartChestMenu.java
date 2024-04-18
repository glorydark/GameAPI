package gameapi.fakeInventory.minecart;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.annotation.Future;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
@Future
public class AdvancedMinecartChestMenu {

    private String title;

    private Map<Integer, Item> inventory = new LinkedHashMap<>();

    private BiConsumer<Player, Item> moveItemBiConsumer = (player, item) -> {};

    private Consumer<Player> closeConsumer = player -> {};

    private boolean movable = false;

    @Setter(AccessLevel.NONE)
    private AdvancedEntityMinecartChest spawnEntity = null;

    public AdvancedMinecartChestMenu(String title) {
        this.title = title;
    }

    public void setInventory(Map<Integer, Item> inventory) {
        this.inventory = inventory;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void onMoveItemRespond(BiConsumer<Player, Item> consumer) {
        this.moveItemBiConsumer = consumer;
    }

    public void onCloseConsumer(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
    }

    public BiConsumer<Player, Item> getMoveItemBiConsumer() {
        return moveItemBiConsumer;
    }

    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<Integer, Item> getInventory() {
        return inventory;
    }

    public boolean isMovable() {
        return movable;
    }

    public void showToPlayer(Player player) {
        AdvancedEntityMinecartChest chest = new AdvancedEntityMinecartChest(player.getChunk(), EntityMinecartChest.getDefaultNBT(player.getPosition()), player);
        chest.namedTag.putList(new ListTag("Items"));
        chest.namedTag.putByte("Slot", 27);
        chest.namedTag.putBoolean("Invulnerable", true);
        chest.namedTag.putBoolean("CustomDisplayTile", false);
        chest.getInventory().setContents(this.inventory);
        chest.setNameTag(this.title);
        chest.setNameTagVisible(false);
        chest.setNameTagAlwaysVisible(false);
        chest.setImmobile(true);
        chest.spawnTo(player);
        player.addWindow(chest.getInventory());
        // todo: 加上那啥...监听，对。listener补齐。
        spawnEntity = chest;
    }
}
