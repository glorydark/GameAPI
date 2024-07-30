package gameapi.form.response;

import cn.nukkit.item.Item;
import gameapi.form.inventory.block.AdvancedFakeBlockInventory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class BlockInventoryResponse {

    private AdvancedFakeBlockInventory inventory;

    private int slot;

    private Item item;
}
