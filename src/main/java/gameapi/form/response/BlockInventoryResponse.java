package gameapi.form.response;

import cn.nukkit.item.Item;
import gameapi.form.inventory.block.AdvancedBlockFakeBlockInventory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class BlockInventoryResponse {

    private AdvancedBlockFakeBlockInventory inventory;

    private int slot;

    private Item item;

    private boolean cancelled;

    public BlockInventoryResponse(AdvancedBlockFakeBlockInventory inventory, int slot, Item item) {
        this.inventory = inventory;
        this.slot = slot;
        this.item = item;
    }
}
