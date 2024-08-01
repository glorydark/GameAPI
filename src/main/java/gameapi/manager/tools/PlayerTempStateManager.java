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

    public static final String KEY_OFFHAND_CACHES = "offhand_caches";

    public static final String KEY_ENDER_CHEST_CACHE = "ender_chest_caches";

    public static final String KEY_EXP = "exp";

    public static final String KEY_EXP_LEVEL = "exp_level";

    public static void recoverData(Player player) {
        if (!GameAPI.getInstance().isSaveTempStates()) {
            return;
        }
        loadBag(player); // offhand & player inventory
        loadExpCaches(player);
        loadEnderChestCaches(player);
    }

    public static void saveAllData(Player player) {
        if (!GameAPI.getInstance().isSaveTempStates()) {
            return;
        }
        saveBag(player);
        saveExpCaches(player);
        saveEnderChestCaches(player);
    }

    protected static void loadBag(Player player) {
        if (PlayerTempStateManager.getPlayerConfig(player, KEY_BAG_CACHES) != null) {
            PlayerTempStateManager.loadInventoryCaches(player);
        }
        if (PlayerTempStateManager.getPlayerConfig(player, KEY_OFFHAND_CACHES) != null) {
            PlayerTempStateManager.loadOffhandCaches(player);
        }
        Server.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("baseEvent.join.bag_cache_existed", player.getName()));
    }

    protected static void saveBag(Player player) {
        PlayerTempStateManager.saveInventoryCaches(player);
        PlayerTempStateManager.saveOffhandData(player);
    }

    protected static void saveOffhandData(Player player) {
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getOffhandInventory().getSize(); i++) {
            Item item = player.getOffhandInventory().getItem(i);
            bag.add(ItemTools.toString(item));
        }
        setPlayerTempDataConfig(player, KEY_OFFHAND_CACHES, bag);
    }

    protected static void loadOffhandCaches(Player player) {
        List<String> bag = getPlayerConfig(player, KEY_OFFHAND_CACHES, new ArrayList<>());
        if (bag != null && bag.size() > 0) {
            for (int i = 0; i < player.getOffhandInventory().getSize(); i++) {
                player.getOffhandInventory().setItem(i, ItemTools.toItem(bag.get(i)));
            }
            removePlayerTempDataConfig(player, KEY_OFFHAND_CACHES);
        }
    }

    protected static void saveEnderChestCaches(Player player) {
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getEnderChestInventory().getSize() + 4; i++) {
            Item item = player.getEnderChestInventory().getItem(i);
            bag.add(ItemTools.toString(item));
        }
        setPlayerTempDataConfig(player, KEY_ENDER_CHEST_CACHE, bag);
    }

    protected static void loadEnderChestCaches(Player player) {
        List<String> bag = getPlayerConfig(player, KEY_ENDER_CHEST_CACHE, new ArrayList<>());
        if (bag != null && bag.size() > 0) {
            for (int i = 0; i < player.getEnderChestInventory().getSize() + 4; i++) {
                player.getEnderChestInventory().setItem(i, ItemTools.toItem(bag.get(i)));
            }
            removePlayerTempDataConfig(player, KEY_ENDER_CHEST_CACHE);
        }
    }

    protected static void saveInventoryCaches(Player player) {
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            Item item = player.getInventory().getItem(i);
            bag.add(ItemTools.toString(item));
        }
        setPlayerTempDataConfig(player, KEY_BAG_CACHES, bag);
    }

    protected static void loadInventoryCaches(Player player) {
        List<String> bag = getPlayerConfig(player, KEY_BAG_CACHES, new ArrayList<>());
        if (bag != null && bag.size() > 0) {
            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                player.getInventory().setItem(i, ItemTools.toItem(bag.get(i)));
            }
            removePlayerTempDataConfig(player, KEY_BAG_CACHES);
        }
    }

    protected static void saveExpCaches(Player player) {
        setPlayerTempDataConfig(player, KEY_EXP, player.getExperience());
        setPlayerTempDataConfig(player, KEY_EXP_LEVEL, player.getExperienceLevel());
    }

    protected static void loadExpCaches(Player player) {
        int exp = getPlayerConfig(player, KEY_EXP, -1);
        int level = getPlayerConfig(player, KEY_EXP_LEVEL, -1);
        if (exp != -1 && level != -1) {
            player.setExperience(exp, level);
            removePlayerTempDataConfig(player, KEY_BAG_CACHES);
        }
    }

    protected static Object getPlayerConfig(Player player, String key) {
        return getPlayerConfig(player, key, null);
    }

    protected static <T> T getPlayerConfig(Player player, String key, T defaultValue) {
        Config config = new Config(GameAPI.getPath() + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        return config.get(key, defaultValue);
    }

    protected static void setPlayerTempDataConfig(Player player, String key, Object value) {
        Config config = new Config(GameAPI.getPath() + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.set(key, value);
        config.save();
    }

    protected static void removePlayerTempDataConfig(Player player, String key) {
        Config config = new Config(GameAPI.getPath() + "/player_caches/" + player.getName() + ".yml", Config.YAML);
        config.remove(key);
        config.save();
    }
}
