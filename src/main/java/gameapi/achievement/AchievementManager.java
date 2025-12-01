package gameapi.achievement;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;

import java.io.File;
import java.util.*;

/**
 * @author glorydark
 */
public class AchievementManager {

    protected static Map<AchievementCategoryData, Map<String, AchievementData>> achievements = new LinkedHashMap<>();

    protected static Map<String, ConfigSection> ownedAchievementCaches = new LinkedHashMap<>();

    public static void load() {
        achievements.clear();
        ownedAchievementCaches.clear();
        new File(GameAPI.getPath() + "/achievements/").mkdirs();
        File playerDataDir = new File(GameAPI.getPath() + "/achievements/player_data/");
        File categoryDir = new File(GameAPI.getPath() + "/achievements/category/");
        playerDataDir.mkdirs();
        categoryDir.mkdirs();
        for (File file : Objects.requireNonNull(categoryDir.listFiles())) {
            Config config = new Config(file, Config.YAML);
            String categoryId = file.getName().substring(0, file.getName().lastIndexOf("."));
            AchievementCategoryData achievementCategoryData = new AchievementCategoryData(
                    categoryId,
                    config.getString("display_name"),
                    config.getString("icon_path_type"),
                    config.getString("icon_path")
            );
            List<Map<String, Object>> achievementList = config.get("list", new ArrayList<>());
            for (Map<String, Object> map : achievementList) {
                String achievementId = (String) map.getOrDefault("id", "null");
                achievements.computeIfAbsent(achievementCategoryData, s -> new LinkedHashMap<>())
                        .put(achievementId, new AchievementData(
                                categoryId,
                                achievementId,
                                (String) map.getOrDefault("display_name", "null"),
                                (String) map.getOrDefault("description", "null"),
                                (String) map.getOrDefault("icon_path_type", ""),
                                (String) map.getOrDefault("icon_path", "")
                        ));
            }
        }
        for (File file : Objects.requireNonNull(playerDataDir.listFiles())) {
            Config config = new Config(file, Config.YAML);
            String achievementId = file.getName().substring(0, file.getName().lastIndexOf("."));
            ownedAchievementCaches.put(achievementId, config.getRootSection());
        }
        GameAPI.getInstance().getLogger().info(TextFormat.GREEN + "成功加载 " + achievements.size() + " 个成就类别及其下属成就！");
    }

    public static Map<AchievementCategoryData, Map<String, AchievementData>> getAchievements() {
        return achievements;
    }

    public static Map<String, ConfigSection> getOwnedAchievementCaches() {
        return ownedAchievementCaches;
    }

    public static void endowAchievement(String player, String category, String achievementId, String reason) {
        long endowMillis = System.currentTimeMillis();
        ConfigSection section = ownedAchievementCaches.computeIfAbsent(category, s -> new ConfigSection());

        ConfigSection subSection = section.getSection(achievementId);
        subSection.set(player, new ConfigSection() {
            {
                this.put("time_millis", endowMillis);
                this.put("reason", reason);
            }
        });
        section.set(achievementId, subSection);

        Config config = new Config(GameAPI.getPath() + File.separator + "achievements" + File.separator + "player_data" + File.separator + category + ".yml", Config.YAML);
        config.setAll(section);
        config.save();
        GameAPI.getGameDebugManager().info(TextFormat.GREEN + "成功给予玩家 " + player + " 成就 " + achievementId + ", 所属类别：" + category + ", 原因：" + reason);
    }

    public static void removeAchievement(String player, String category, String achievementId) {
        ConfigSection section = ownedAchievementCaches.computeIfAbsent(category, s -> new ConfigSection());
        Config config = new Config(GameAPI.getPath() + File.separator + "achievements" + File.separator + "player_data" + File.separator + category + ".yml", Config.YAML);

        section.getSection(achievementId).remove(player);
        config.setAll(section);
        config.save();
    }

    public static boolean hasAchievement(String player, String category, String achievementId) {
        ConfigSection section = ownedAchievementCaches.computeIfAbsent(category, s -> new ConfigSection());
        return section.getSection(achievementId).containsKey(player);
    }

    public static Long getEndowedAchievementTimeMillis(String player, String category, String achievementId) {
        ConfigSection section = ownedAchievementCaches.computeIfAbsent(category, s -> new ConfigSection());
        return section.getSection(achievementId).getSection(player).get("time_millis", null);
    }
}
