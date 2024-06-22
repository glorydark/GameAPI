package gameapi.extensions.supplyChest;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SupplyItem {

    protected Item item;

    protected double possibility; // 0-1
}