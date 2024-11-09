package gameapi.manager.data.activity;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.manager.data.GameActivityManager;
import gameapi.tools.CalendarTools;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
@Getter
public class ActivityData {

    private final String activityId;

    private final String name;

    private final String description;

    private final long startTime;

    private final long endTime;

    private final List<AwardData> awardDataList = new ArrayList<>();

    public Map<String, ActivityPlayerDataCache> activityPlayerDataCacheMap = new LinkedHashMap<>();

    public ActivityData(String activityId, String name, String description, String startTime, String endTime) {
        this.activityId = activityId;
        this.name = name;
        this.description = description;
        this.startTime = CalendarTools.getDate(startTime).getTime();
        this.endTime = CalendarTools.getDate(endTime).getTime();
    }

    public void setData(String player, ConfigSection section) {
        Config file = new Config(GameActivityManager.path + File.separator + this.activityId + File.separator + player + ".yml", Config.YAML);
        file.setAll(section);
        file.save();
        this.activityPlayerDataCacheMap.put(player, new ActivityPlayerDataCache(section, System.currentTimeMillis()));
    }

    public void addAward(AwardData awardData) {
        this.awardDataList.add(awardData);
    }

    public ConfigSection getData(String player) {
        if (this.activityPlayerDataCacheMap.containsKey(player)) {
            return this.activityPlayerDataCacheMap.get(player).getSection();
        } else {
            ConfigSection result;
            File file = new File(GameActivityManager.path + File.separator + this.activityId + File.separator + player + ".yml");
            if (file.exists()) {
                result = new Config(file, Config.YAML).getRootSection();
                this.activityPlayerDataCacheMap.put(player, new ActivityPlayerDataCache(result, System.currentTimeMillis()));
                return result;
            }
            result = new ConfigSection();
            this.activityPlayerDataCacheMap.put(player, new ActivityPlayerDataCache(result, System.currentTimeMillis()));
            return result;
        }
    }

    public void showActivityWindow(Player player) {
        if (System.currentTimeMillis() < this.startTime) {
            player.sendMessage("活动未开始！");
            return;
        } else if (System.currentTimeMillis() > this.endTime) {
            player.sendMessage("活动已结束！");
            return;
        }
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(this.name, this.description);
        for (AwardData awardData : this.awardDataList) {
            simple.addButton(awardData.getElementButton(player));
        }
        simple.showToPlayer(player);
    }
}
