package gameapi.extensions.supplyChest;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import gameapi.event.room.RoomSupplyChestRefreshEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class SupplyChest {

    Room room;

    Location location;

    List<SupplyItem> supplyItemList;

    protected long lastUpdateMillis;

    int maxItemCount;

    long startCoolDownMillis;

    long intervalTicks; // If this equal to zero, it would not regenerate again.

    public SupplyChest(Room room, Location location, int maxItemCount, long startCoolDownMillis, long intervalTicks) {
        this(room, location, new ArrayList<>(), maxItemCount, startCoolDownMillis, intervalTicks);
    }

    public SupplyChest(Room room, Location location, List<SupplyItem> supplyChests, int maxItemCount, long startCoolDownMillis, long intervalTicks) {
        this.room = room;
        this.location = location;
        this.supplyItemList = supplyChests;
        this.maxItemCount = maxItemCount;
        this.startCoolDownMillis = startCoolDownMillis;
        this.intervalTicks = intervalTicks;
    }

    public boolean isCoolDownEnd() {
        if (intervalTicks > 0) {
            return this.getCoolDown() >= intervalTicks;
        } else {
            return false;
        }
    }

    public long getCoolDown() {
        return System.currentTimeMillis() - lastUpdateMillis;
    }

    public void onUpdate() {
        // If it is not allowed to refresh, it will no longer refresh the items in the chest
        if (!this.isRefreshable()) {
            return;
        }
        // If the subtraction is less than intervalTicks, it will not refresh the supplies in the chest
        if (this.getCoolDown() < intervalTicks) {
            return;
        }
        // Update the latestMillis
        BlockEntityChest entityChest = this.getEntity();
        if (entityChest != null) {
            entityChest.getInventory().clearAll();
            // Try to get the refreshments
            int count = 0;
            List<Item> supplyItems = new ArrayList<>();
            for (SupplyItem supplyItem : supplyItemList) {
                if (count >= maxItemCount) {
                    return;
                }
                if (this.processFakeRandom(supplyItem.getPossibility())) {
                    supplyItems.add(supplyItem.getItem());
                    count++;
                }
            }
            RoomSupplyChestRefreshEvent event = new RoomSupplyChestRefreshEvent(room, this, supplyItemList);
            GameListenerRegistry.callEvent(room, event);
            if (!event.isCancelled()) {
                lastUpdateMillis = System.currentTimeMillis();
                for (Item supplyItem : supplyItems) {
                    entityChest.getInventory().addItem(supplyItem);
                }
            }
        }
    }

    public boolean isRefreshable() {
        return room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart
                && System.currentTimeMillis() >= room.getStartMillis() + startCoolDownMillis
                && intervalTicks != 0;
    }

    public BlockEntityChest getEntity() {
        Block block = location.getLevel().getBlock(location.getLocation());
        if (block.getId() == 54) {
            return (BlockEntityChest) location.getLevel().getBlockEntity(location.getLocation());
        }
        return null;
    }

    /**
     * This is a method to get a random choice about whether it hits a winning streak or it is on a losing one.
     */
    public boolean processFakeRandom(int possibilities) {
        return ThreadLocalRandom.current().nextInt(1, possibilities + 1) <= possibilities;
    }
}
