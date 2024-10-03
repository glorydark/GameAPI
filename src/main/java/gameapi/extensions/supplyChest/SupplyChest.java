package gameapi.extensions.supplyChest;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;
import gameapi.event.room.RoomSupplyChestRefreshEvent;
import gameapi.extensions.supplyChest.item.SupplyItem;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Experimental
public class SupplyChest {
    private Room room;
    private Location location;
    private List<SupplyItem> supplyItemList;
    private List<SupplyItem> boundItemList = new ArrayList<>();
    private int maxItemCount;

    public SupplyChest(Room room, Location location, int maxItemCount) {
        this(room, location, new ArrayList<>(), maxItemCount);
    }

    public SupplyChest(Room room, Location location, List<SupplyItem> supplyChests, int maxItemCount) {
        this.room = room;
        this.location = location;
        this.supplyItemList = supplyChests;
        this.maxItemCount = maxItemCount;
    }

    public SupplyChest maxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
        return this;
    }

    public SupplyChest boundItem(SupplyItem... boundItems) {
        this.boundItemList.addAll(Arrays.asList(boundItems));
        return this;
    }

    public void clear() {
        BlockEntityChest entityChest = this.getEntity();
        if (entityChest != null) {
            entityChest.getInventory().clearAll();
            entityChest.saveNBT();
        }
    }

    public void refresh() {
        BlockEntityChest entityChest = this.getEntity();
        if (entityChest != null) {
            // Try to get the refreshments
            int count = 0;
            List<Item> supplyItems = new ArrayList<>();
            for (SupplyItem supplyItem : this.boundItemList) {
                supplyItems.add(supplyItem.select());
                count++;
            }
            Collections.shuffle(this.supplyItemList);
            for (SupplyItem supplyItem : this.supplyItemList) {
                if (count >= this.maxItemCount) {
                    break;
                }
                if (this.processFakeRandom(supplyItem.getPossibility())) {
                    supplyItems.add(supplyItem.select());
                    count++;
                }
            }
            RoomSupplyChestRefreshEvent event = new RoomSupplyChestRefreshEvent(this.room, this, this.supplyItemList);
            GameListenerRegistry.callEvent(this.room, event);
            if (!event.isCancelled()) {
                entityChest.getInventory().clearAll();
                for (Item supplyItem : supplyItems) {
                    entityChest.getInventory().setItem(getRandomSlot(), supplyItem);
                }
            }
        } else {
            GameAPI.getGameDebugManager().error("Cannot find the chest at " + location.asBlockVector3().asVector3().toString());
        }
    }

    public BlockEntityChest getEntity() {
        Block block = this.location.getLevel().getBlock(this.location.getLocation());
        if (block.getId() == 54 || block.getId() == 146) {
            return (BlockEntityChest) this.location.getLevel().getBlockEntity(this.location.getLocation());
        }
        return null;
    }

    /**
     * This is a method to get a random choice about whether it hits a winning streak or it is on a losing one.
     */
    public boolean processFakeRandom(double possibilities) {
        return ThreadLocalRandom.current().nextDouble() <= possibilities;
    }

    protected int getRandomSlot() {
        int slots = getEntity().getInventory().getSize();
        int randSlot = ThreadLocalRandom.current().nextInt(slots);
        if (this.getEntity().getInventory().getItemFast(randSlot).getId() != 0) {
            return getRandomSlot();
        }
        return randSlot;
    }
}