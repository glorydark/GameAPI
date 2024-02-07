package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
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

    public static final String KEY_BAG_CACHES = "bag_caches";

    public static void loadAllData(Player player) {
        if (GameAPI.saveBag) {
            loadBag(player);
        }
    }

    public static void saveAllData(Player player) {
        if (GameAPI.saveBag) {
            saveBagData(player);
        }
    }

    protected static void loadBag(Player player) {
        if (PlayerTempStateManager.getPlayerConfig(player, KEY_BAG_CACHES) != null) {
            PlayerTempStateManager.loadBagCaches(player);
            player.getFoodData().setLevel(20, 20.0F);
            Server.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("baseEvent.join.bagCacheExisted", player.getName()));
        }
    }

    public static void saveBagData(Player player) {
        if (GameAPI.saveBag) {
            List<String> bag = new ArrayList<>();
            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                Item item = player.getInventory().getItem(i);
                bag.add(InventoryTools.toBase64String(item));
            }
            savePlayerBagConfig(player, KEY_BAG_CACHES, bag);
        }
        player.getInventory().clearAll();
    }

    protected static void loadBagCaches(Player player) {
        if (GameAPI.saveBag) {
            List<String> bag = (List<String>) getPlayerConfig(player, KEY_BAG_CACHES, new ArrayList<>());
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
                removePlayerBagConfig(player, "bag_caches");
            }
        }
    }

    protected static Object getPlayerConfig(Player player, String key) {
        return getPlayerConfig(player, key, null);
    }

    protected static Object getPlayerConfig(Player player, String key, Object defaultValue) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        return config.get(key, defaultValue);
    }

    protected static void savePlayerBagConfig(Player player, String key, Object value) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.set(key, value);
        config.save();
    }

    protected static void removePlayerBagConfig(Player player, String key) {
        Config config = new Config(GameAPI.path + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.remove(key);
        config.save();
    }
}
