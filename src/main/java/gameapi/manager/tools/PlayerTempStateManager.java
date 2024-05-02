package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;
import gameapi.tools.ItemTools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class PlayerTempStateManager {

    public static final String KEY_BAG_CACHES = "bag_caches";

    @Experimental
    public static void recoverData(Player player) {
        loadBag(player);
    }

    @Experimental
    public static void saveAllData(Player player) {
        saveBagData(player);
    }

    protected static void loadBag(Player player) {
        if (PlayerTempStateManager.getPlayerConfig(player, KEY_BAG_CACHES) != null) {
            PlayerTempStateManager.loadBagCaches(player);
            player.getFoodData().setLevel(20, 20.0F);
            Server.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("baseEvent.join.bag_cache_existed", player.getName()));
        }
    }

    public static void saveBagData(Player player) {
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            Item item = player.getInventory().getItem(i);
            bag.add(ItemTools.toString(item));
        }
        savePlayerBagConfig(player, KEY_BAG_CACHES, bag);
    }

    protected static void loadBagCaches(Player player) {
        List<String> bag = (List<String>) getPlayerConfig(player, KEY_BAG_CACHES, new ArrayList<>());
        if (bag != null && bag.size() > 0) {
            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                player.getInventory().setItem(i, ItemTools.toItem(bag.get(i)));
            }
            removePlayerBagConfig(player, KEY_BAG_CACHES);
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
