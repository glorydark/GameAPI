package gameapi.manager.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.Data;

import java.util.Map;

/**
 * @author glorydark
 */
@Data
public class PlayerTempData {

    private final Map<Integer, Item> inventoryContents;

    private final Map<Integer, Item> enderChestInventoryContents;

    private final Map<Integer, Item> offhandInventoryContents;

    private final int level;

    private final int exp;

    public PlayerTempData(Player player) {
        this.inventoryContents = player.getInventory().getContents();
        this.enderChestInventoryContents = player.getEnderChestInventory().getContents();
        this.offhandInventoryContents = player.getOffhandInventory().getContents();
        this.level = player.getExperienceLevel();
        this.exp = player.getExperience();
    }

    public PlayerTempData(Map<Integer, Item> inventoryContents, Map<Integer, Item> enderChestInventoryContents, Map<Integer, Item> offhandInventoryContents, int level, int exp) {
        this.inventoryContents = inventoryContents;
        this.enderChestInventoryContents = enderChestInventoryContents;
        this.offhandInventoryContents = offhandInventoryContents;
        this.level = level;
        this.exp = exp;
    }
}
