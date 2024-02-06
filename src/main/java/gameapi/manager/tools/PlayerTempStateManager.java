package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.tools.InventoryTools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class PlayerTempStateManager {

    public static void saveAllData(Player player) {

    }

    public static void saveBagData(Player player) {
        if (GameAPI.saveBag) {
            List<String> bag = new ArrayList<>();
            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                Item item = player.getInventory().getItem(i);
                bag.add(InventoryTools.toBase64String(item));
            }
            savePlayerBagConfig(player, bag);
        }
        player.getInventory().clearAll();
    }

    public static void loadBag(Player player) {
        if (GameAPI.saveBag) {
            List<String> bag = getPlayerBagConfig(player);
            if (bag != null && bag.size() > 0) {
                player.getInventory().clearAll();
                for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                    String[] a = bag.get(i).split(":");
                    Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
                    if (a.length > 3 && !a[3].equals("null")) {
                        CompoundTag tag = Item.parseCompoundTag(InventoryTools.hexStringToBytes(a[3]));
                        item.setNamedTag(tag);
                    }
                    player.getInventory().setItem(i, item);
                }
                removePlayerBagConfig(player);
            }
        }
    }

    public static List<String> getPlayerBagConfig(Player player) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        if (!config.exists("bagCache")) {
            return null;
        }
        return new ArrayList<>(config.getStringList("bagCache"));
    }

    public static void savePlayerBagConfig(Player player, List<String> content) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.set("bagCache", content);
        config.save();
    }

    public static void removePlayerBagConfig(Player player) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.remove("bagCache");
        config.save();
    }
}
