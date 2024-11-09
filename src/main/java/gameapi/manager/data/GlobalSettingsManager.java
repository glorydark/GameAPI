package gameapi.manager.data;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import gameapi.GameAPI;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class GlobalSettingsManager {

    public static String path;

    public static Map<String, Map<String, ConfigSection>> playerSettingCaches = new LinkedHashMap<>();

    public static void init() {
        path = GameAPI.getPath() + File.separator + "global_settings" + File.separator;
        new File(path).mkdirs();
    }

    public static void loadPlayerData(Player player) {
        File file = getPlayerSettingFile(player);
        if (file.exists()) {
            Config config = new Config(file, Config.YAML);
            for (String name : config.getKeys(false)) {
                playerSettingCaches.computeIfAbsent(player.getName(), (s) -> new LinkedHashMap<>()).put(name, config.getSection(name));
            }
        }
    }

    public static void removeCacheAndSaveData(Player player) {
        Map<String, ConfigSection> subMap = playerSettingCaches.get(player.getName());
        if (subMap != null) {
            saveData(player, subMap.keySet().toArray(new String[0]));
        }
    }

    public static void saveData(Player player, String... names) {
        Config saveConf = new Config(getPlayerSettingFile(player), Config.YAML);
        for (String name : names) {
            ConfigSection configSection = getPlayerSettingCache(player, name);
            if (configSection != null) {
                if (configSection.isEmpty()) {
                    File file = getPlayerSettingFile(player);
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    saveConf.set(name, configSection);
                }
            }
        }
        saveConf.save();
    }

    public static File getPlayerSettingFile(Player player) {
        return new File(path + "/" + player.getName() + ".yml");
    }

    public static ConfigSection getPlayerSettingCache(Player player, String name) {
        return playerSettingCaches.getOrDefault(player.getName(), new LinkedHashMap<>()).getOrDefault(name, new ConfigSection());
    }

    public static void setPlayerSettingCache(Player player, String name, ConfigSection configSection) {
        playerSettingCaches.computeIfAbsent(player.getName(), s -> new LinkedHashMap<>()).put(name, configSection);
    }
}
