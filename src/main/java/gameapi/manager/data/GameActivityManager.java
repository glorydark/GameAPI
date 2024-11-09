package gameapi.manager.data;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.manager.data.activity.ActivityData;
import gameapi.manager.data.activity.ActivityPlayerDataCache;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class GameActivityManager {

    public static String path;

    public static Map<String, ActivityData> activityDataMap = new LinkedHashMap<>();

    public static void init() {
        path = GameAPI.getPath() + File.separator + "activities" + File.separator;
    }

    public static void registerActivity(ActivityData activityData) {
        new File(path + activityData.getActivityId() + File.separator).mkdirs();
        activityDataMap.putIfAbsent(activityData.getActivityId(), activityData);
    }

    public static ActivityData getActivity(String activityId) {
        return activityDataMap.get(activityId);
    }

    public static void updateTempDataCleaning() {
        for (Map.Entry<String, ActivityData> entry : activityDataMap.entrySet()) {
            Map<String, ActivityPlayerDataCache> tempDataMap = entry.getValue().activityPlayerDataCacheMap;
            for (Map.Entry<String, ActivityPlayerDataCache> entry1 : new ArrayList<>(tempDataMap.entrySet())) {
                if (entry1.getValue().isExpired()) {
                    tempDataMap.remove(entry1.getKey());
                    GameAPI.getGameDebugManager().info("Delete expired temp cache for player " + entry1.getKey() + " in activity - id: " + entry.getKey());
                }
            }
        }
    }

    public static void showActivityForm(Player player, String activityId) {
        ActivityData activityData = getActivity(activityId);
        if (activityData != null) {
            activityData.showActivityWindow(player);
        }
    }
}
