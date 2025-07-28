package gameapi.tools;

import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author glorydark
 */
public class InventoryTools {

    public static Map<Integer, Item> getPlayerInventoryItems(String name) {
        if (Server.getInstance().lookupName(name).isPresent()) {
            Player player1 = Server.getInstance().getPlayer(name);
            if (player1 != null) {
                return player1.getInventory().getContents();
            } else {
                Map<Integer, Item> contents = new LinkedHashMap<>();
                OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(name);
                if (offlinePlayer != null) {
                    CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
                    ListTag<CompoundTag> inventory = namedTag.getList("Inventory", CompoundTag.class);
                    for (int i = 0; i < inventory.size(); i++) {
                        CompoundTag testTag = inventory.get(i);
                        Item item = NBTIO.getItemHelper(testTag);
                        contents.put(i, item);
                    }
                }
                return contents;
            }
        } else {
            return new LinkedHashMap<>();
        }
    }

    public static Map<Integer, Item> getPlayerEnderChestItems(String name) {
        Optional<Player> seePlayer = Optional.ofNullable(Server.getInstance().getPlayer(name));
        if (seePlayer.isPresent()) {
            return new LinkedHashMap<>(seePlayer.get().getEnderChestInventory().slots);
        } else {
            OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(name);
            if (offlinePlayer != null) {
                Map<Integer, Item> contents = new LinkedHashMap<>();
                CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
                ;

                if (namedTag.contains("EnderItems") && namedTag.get("EnderItems") instanceof ListTag) {
                    ListTag<CompoundTag> inventoryList = namedTag.getList("EnderItems", CompoundTag.class);
                    for (CompoundTag compoundTag : inventoryList.getAll()) {
                        Item item = NBTIO.getItemHelper(compoundTag);
                        contents.put(compoundTag.getByte("Slot"), item);
                    }
                }

                return contents;
            } else {
                return new LinkedHashMap<>();
            }
        }
    }
}
