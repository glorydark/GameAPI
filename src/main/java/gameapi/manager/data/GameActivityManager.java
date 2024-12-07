package gameapi.manager.data;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.activity.ActivityData;
import gameapi.manager.data.activity.ActivityPlayerDataCache;
import gameapi.manager.data.activity.ActivityRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class GameActivityManager {

    protected static String STATUS_UNDER_PREPARATION = "§c[未开始]";
    protected static String STATUS_START = "§e[进行中]";
    protected static String STATUS_EXPIRED = "§c[已结束]";

    public static String path;

    public static Map<String, ActivityData> activityDataMap = new LinkedHashMap<>();

    public static void init() {
        path = GameAPI.getPath() + File.separator + "activities" + File.separator;

        ActivityRegistry.init();
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

    public static void showAllActivityForm(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("活动界面", "");
        for (Map.Entry<String, ActivityData> entry : activityDataMap.entrySet()) {
            ActivityData activityData = entry.getValue();
            simple.addButton(new ResponsiveElementButton(
                    (activityData.isStarted()? (activityData.isExpired()? STATUS_EXPIRED: STATUS_START): STATUS_UNDER_PREPARATION)
                            + activityData.getName()
                            + "\n" + activityData.getDescription()));
        }
        simple.showToPlayer(player);
    }
}
