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

    public static final String KEY_EXP = "exp";

    public static final String KEY_EXP_LEVEL = "exp_level";

    @Experimental
    public static void recoverData(Player player) {
        loadBag(player);
        loadExpCaches(player);
    }

    @Experimental
    public static void saveAllData(Player player) {
        saveBagData(player);
        saveExpCaches(player);
    }

    protected static void loadBag(Player player) {
        if (PlayerTempStateManager.getPlayerConfig(player, KEY_BAG_CACHES) != null) {
            PlayerTempStateManager.loadBagCaches(player);
            Server.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("baseEvent.join.bag_cache_existed", player.getName()));
        }
    }

    protected static void saveBagData(Player player) {
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            Item item = player.getInventory().getItem(i);
            bag.add(ItemTools.toString(item));
        }
        savePlayerBagConfig(player, KEY_BAG_CACHES, bag);
    }

    protected static void loadBagCaches(Player player) {
        List<String> bag = getPlayerConfig(player, KEY_BAG_CACHES, new ArrayList<>());
        if (bag != null && bag.size() > 0) {
            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                player.getInventory().setItem(i, ItemTools.toItem(bag.get(i)));
            }
            removePlayerBagConfig(player, KEY_BAG_CACHES);
        }
    }

    protected static void saveExpCaches(Player player) {
        savePlayerBagConfig(player, KEY_EXP, player.getExperience());
        savePlayerBagConfig(player, KEY_EXP_LEVEL, player.getExperienceLevel());
    }

    protected static void loadExpCaches(Player player) {
        int exp = getPlayerConfig(player, KEY_EXP, -1);
        int level = getPlayerConfig(player, KEY_EXP_LEVEL, -1);
        if (exp != -1 && level != -1) {
            player.setExperience(exp, level);
            removePlayerBagConfig(player, KEY_BAG_CACHES);
        }
    }

    protected static Object getPlayerConfig(Player player, String key) {
        return getPlayerConfig(player, key, null);
    }

    protected static <T> T getPlayerConfig(Player player, String key, T defaultValue) {
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
