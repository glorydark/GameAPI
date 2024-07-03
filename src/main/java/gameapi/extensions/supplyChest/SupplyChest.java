package gameapi.extensions.supplyChest;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import gameapi.event.room.RoomSupplyChestRefreshEvent;
import gameapi.extensions.supplyChest.item.SupplyItem;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class SupplyChest {

    protected long lastUpdateMillis;
    private Room room;
    private Location location;
    private List<SupplyItem> supplyItemList;
    private int maxItemCount;

    private long startCoolDownMillis;

    private long intervalTicks; // If this equal to zero, it would not regenerate again.

    private int maxRefreshTimes;

    private int refreshedTimes = 0;

    public SupplyChest(Room room, Location location, int maxItemCount, long startCoolDownMillis, long intervalTicks, int maxRefreshTimes) {
        this(room, location, new ArrayList<>(), maxItemCount, startCoolDownMillis, intervalTicks, maxRefreshTimes);
    }

    public SupplyChest(Room room, Location location, List<SupplyItem> supplyChests, int maxItemCount, long startCoolDownMillis, long intervalTicks, int maxRefreshTimes) {
        this.room = room;
        this.location = location;
        this.supplyItemList = supplyChests;
        this.maxItemCount = maxItemCount;
        this.startCoolDownMillis = startCoolDownMillis;
        this.intervalTicks = intervalTicks;
        this.maxRefreshTimes = maxRefreshTimes;
    }

    public boolean isCoolDownEnd() {
        if (this.intervalTicks > 0) {
            return this.getLastUpdateDiff() >= this.intervalTicks;
        } else {
            return false;
        }
    }

    public long getLastUpdateDiff() {
        return System.currentTimeMillis() - this.lastUpdateMillis;
    }

    public void onUpdate() {
        // If it is not allowed to refresh, it will no longer refresh the items in the chest
        if (!this.isRefreshable()) {
            return;
        }
        // If the subtraction is less than intervalTicks, it will not refresh the supplies in the chest
        if (this.getLastUpdateDiff() < this.intervalTicks) {
            return;
        }
        // Update the latestMillis
        BlockEntityChest entityChest = this.getEntity();
        if (entityChest != null) {
            // Try to get the refreshments
            int count = 0;
            List<Item> supplyItems = new ArrayList<>();
            for (SupplyItem supplyItem : this.supplyItemList) {
                if (count >= this.maxItemCount) {
                    return;
                }
                if (this.processFakeRandom(supplyItem.getPossibility())) {
                    supplyItems.add(supplyItem.select());
                    count++;
                }
            }
            RoomSupplyChestRefreshEvent event = new RoomSupplyChestRefreshEvent(room, this, supplyItemList);
            GameListenerRegistry.callEvent(this.room, event);
            if (!event.isCancelled()) {
                entityChest.getInventory().clearAll();
                this.lastUpdateMillis = System.currentTimeMillis();
                for (Item supplyItem : supplyItems) {
                    entityChest.getInventory().setItem(getRandomSlot(), supplyItem);
                }
            }
        }
    }

    public boolean isRefreshable() {
        return this.room.getRoomStatus() == RoomStatus.ROOM_STATUS_START
                && this.refreshedTimes < this.maxRefreshTimes
                && System.currentTimeMillis() >= this.room.getStartMillis() + this.startCoolDownMillis
                && this.intervalTicks != 0
                && this.isCoolDownEnd();
    }

    public void resetData() {
        this.lastUpdateMillis = -1;
        this.refreshedTimes = 0;
    }

    public BlockEntityChest getEntity() {
        Block block = this.location.getLevel().getBlock(this.location.getLocation());
        if (block.getId() == 54) {
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