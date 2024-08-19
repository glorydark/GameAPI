package gameapi.form.response;

import cn.nukkit.item.Item;
import gameapi.form.inventory.block.AdvancedBlockFakeBlockInventory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class BlockInventoryResponse {

    private AdvancedBlockFakeBlockInventory inventory;

    private int slot;

    private Item item;
}
