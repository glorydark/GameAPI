package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.manager.utils.PlayerTempData;
import gameapi.tools.ItemTools;
import gameapi.tools.SmartTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author glorydark
 */
public class PlayerTempStateManager {

    public static final String KEY_BAG_CACHES = "bag_caches";
    public static final String KEY_OFFHAND_CACHES = "offhand_caches";
    public static final String KEY_ENDER_CHEST_CACHE = "ender_chest_caches";
    public static final String KEY_EXP = "exp";
    public static final String KEY_EXP_LEVEL = "exp_level";

    public static Map<String, PlayerTempData> playerTempDataMap = new LinkedHashMap<>();

    public static void recoverData(Player player) {
        if (!GameAPI.getInstance().isSaveTempStates()) {
            return;
        }
        if (!player.isOnline()) {
            return;
        }
        File file = new File(GameAPI.getPath() + File.separator + "player_caches" + File.separator + player.getName() + ".yml");
        File newPath = new File(GameAPI.getPath() + File.separator + "player_caches_old" + File.separator + player.getName() + File.separator);
        newPath.mkdirs();
        File newSavePath = new File(newPath.getPath() + File.separator + SmartTools.dateToString(Calendar.getInstance().getTime()) + ".yml");
        if (!file.exists()) {
            return;
        }
        if (playerTempDataMap.containsKey(player.getName())) {
            PlayerTempData data = playerTempDataMap.get(player.getName());
            player.getInventory().setContents(data.getInventoryContents());
            player.getOffhandInventory().setContents(data.getOffhandInventoryContents());
            player.getEnderChestInventory().setContents(data.getEnderChestInventoryContents());
            player.setExperience(data.getExp(), data.getLevel());
            playerTempDataMap.remove(player.getName());
        } else {
            Config config = new Config(file, Config.YAML);
            player.getInventory().setContents(loadInventoryCaches(config));
            player.getEnderChestInventory().setContents(loadEnderChestCaches(config));
            player.getOffhandInventory().setContents(loadOffhandCaches(config));
            loadExpCaches(config, player);
        }
        try {
            Files.move(file.toPath(), newSavePath.toPath());
            GameAPI.getGameDebugManager().info("玩家 " + player.getName() + " 还原游戏背包备份成功！");
        } catch (IOException e) {
            file.delete();
            GameAPI.getGameDebugManager().info("玩家 " + player.getName() + " 移动还原游戏背包数据失败，删除背包缓存中...");
        }
    }

    public static void saveAllData(Player player) {
        if (!GameAPI.getInstance().isSaveTempStates()) {
            return;
        }
        playerTempDataMap.put(player.getName(), new PlayerTempData(player));
        Config config = new Config(GameAPI.getPath() + File.separator + "player_caches" + File.separator + player.getName() + ".yml", Config.YAML);
        saveInventoryCaches(config, player);
        saveOffhandData(config, player);
        saveExpCaches(config, player);
        saveEnderChestCaches(config, player);
        config.save();
    }

    @Internal
    public static void saveInventoryCaches(Config config, Player player) {
        Map<Integer, Item> contents = new LinkedHashMap<>(player.getInventory().getContents());
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            Item item = contents.get(i);
            bag.add(ItemTools.toString(item));
        }
        setPlayerTempDataConfig(config, KEY_BAG_CACHES, bag);
    }

    @Internal
    public static void saveOffhandData(Config config, Player player) {
        Map<Integer, Item> contents = new LinkedHashMap<>(player.getOffhandInventory().getContents());
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getOffhandInventory().getSize(); i++) {
            bag.add(ItemTools.toString(contents.get(i)));
        }
        setPlayerTempDataConfig(config, KEY_OFFHAND_CACHES, bag);
    }

    @Internal
    public static void saveEnderChestCaches(Config config, Player player) {
        Map<Integer, Item> contents = new LinkedHashMap<>(player.getEnderChestInventory().getContents());
        List<String> bag = new ArrayList<>();
        for (int i = 0; i < player.getEnderChestInventory().getSize(); i++) {
            bag.add(ItemTools.toString(contents.get(i)));
        }
        setPlayerTempDataConfig(config, KEY_ENDER_CHEST_CACHE, bag);
    }

    @Internal
    public static Map<Integer, Item> loadInventoryCaches(Config config) {
        Map<Integer, Item> inventoryCaches = new LinkedHashMap<>();
        List<String> bag = getPlayerConfig(config, KEY_BAG_CACHES, new ArrayList<>());
        if (!bag.isEmpty()) {
            for (int i = 0; i < bag.size(); i++) {
                inventoryCaches.put(i, ItemTools.toItem(bag.get(i)));
            }
        }
        return inventoryCaches;
    }

    @Internal
    public static Map<Integer, Item> loadOffhandCaches(Config config) {
        Map<Integer, Item> inventoryCaches = new LinkedHashMap<>();
        List<String> bag = getPlayerConfig(config, KEY_OFFHAND_CACHES, new ArrayList<>());
        if (bag != null && !bag.isEmpty()) {
            for (int i = 0; i < bag.size(); i++) {
                inventoryCaches.put(i, ItemTools.toItem(bag.get(i)));
            }
        }
        return inventoryCaches;
    }

    @Internal
    public static Map<Integer, Item> loadEnderChestCaches(Config config) {
        Map<Integer, Item> inventoryCaches = new LinkedHashMap<>();
        List<String> bag = getPlayerConfig(config, KEY_ENDER_CHEST_CACHE, new ArrayList<>());
        if (bag != null && !bag.isEmpty()) {
            for (int i = 0; i < bag.size(); i++) {
                inventoryCaches.put(i, ItemTools.toItem(bag.get(i)));
            }
        }
        return inventoryCaches;
    }

    @Internal
    public static void saveExpCaches(Config config, Player player) {
        setPlayerTempDataConfig(config, KEY_EXP, player.getExperience());
        setPlayerTempDataConfig(config, KEY_EXP_LEVEL, player.getExperienceLevel());
    }

    @Internal
    public static void loadExpCaches(Config config, Player player) {
        player.setExperience(loadExp(config), loadExpLevel(config));
    }

    @Internal
    public static int loadExp(Config config) {
        return getPlayerConfig(config, KEY_EXP, 0);
    }

    @Internal
    public static int loadExpLevel(Config config) {
        return getPlayerConfig(config, KEY_EXP_LEVEL, 0);
    }


    @Internal
    public static Object getPlayerConfig(Config config, String key) {
        return getPlayerConfig(config, key, null);
    }

    @Internal
    public static <T> T getPlayerConfig(Config config, String key, T defaultValue) {
        return config.get(key, defaultValue);
    }

    @Internal
    public static void setPlayerTempDataConfig(Config config, String key, Object value) {
        config.set(key, value);
    }

    @Internal
    public static void removePlayerTempDataConfig(Config config, String key) {
        config.remove(key);
    }
}
