package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.extensions.supplyChest.SupplyChest;
import gameapi.extensions.supplyChest.item.SupplyItem;
import gameapi.room.Room;

import java.util.List;

/**
 * @author glorydark
 */
public class RoomSupplyChestRefreshEvent extends RoomEvent implements Cancellable {

    private final SupplyChest supplyChest;

    private List<SupplyItem> supplyItems;

    public RoomSupplyChestRefreshEvent(Room room, SupplyChest supplyChest, List<SupplyItem> supplyItems) {
        super(room);
        this.supplyChest = supplyChest;
        this.supplyItems = supplyItems;
    }

    public SupplyChest getSupplyChest() {
        return supplyChest;
    }

    public List<SupplyItem> getSupplyItems() {
        return supplyItems;
    }

    public void setSupplyItems(List<SupplyItem> supplyItems) {
        this.supplyItems = supplyItems;
    }
}
