package gameapi.achievement;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.utils.TextFormat;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;

import java.util.*;

/**
 * @author glorydark
 */
public class AchievementUIFactory {

    protected static String UI_STATUS_HAS_ACHIEVEMENT = "[" + TextFormat.GREEN + "已拥有" + TextFormat.RESET + "]";

    protected static String UI_STATUS_UNCLAIMED_ACHIEVEMENT = "[" + TextFormat.RED + "未拥有" + TextFormat.RESET + "]";

    public static void showCategoryMenu(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("成就系统");
        if (AchievementManager.achievements.isEmpty()) {
            simple.setContent("\n\n\n     暂时没有任何成就哦！");
        } else {
            for (Map.Entry<AchievementCategoryData, Map<String, AchievementData>> entry : AchievementManager.getAchievements().entrySet()) {
                simple.addButton(
                        new ResponsiveElementButton(entry.getKey().getDisplayName())
                                .onRespond(player1 -> showCategoryEntriesList(player1, entry.getKey()))
                );
            }
        }
        simple.showToPlayer(player);
    }

    public static void showCategoryEntriesList(Player player, AchievementCategoryData achievementCategoryData) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("成就系統 - " + achievementCategoryData.displayName);
        Map<String, AchievementData> map = AchievementManager.getAchievements().getOrDefault(achievementCategoryData, new LinkedHashMap<>());
        simple.addButton(
                new ResponsiveElementButton("返回")
                        .onRespond(AchievementUIFactory::showCategoryMenu)
        );
        if (map.isEmpty()) {
            simple.setContent("\n\n\n     暂时没有任何成就哦！");
        } else {
            // 根据是否符合条件的布尔值排序，符合条件的在前
            map.entrySet().stream().sorted((m1, m2) -> {
                AchievementData p1 = m1.getValue();
                AchievementData p2 = m2.getValue();
                boolean hasP1 = AchievementManager.hasAchievement(player.getName(), p1.getCategory(), p1.getId());
                boolean hasP2 = AchievementManager.hasAchievement(player.getName(), p2.getCategory(), p2.getId());
                if (hasP1 && !hasP2) {
                    return -1;
                } else if (!hasP1 && hasP2) {
                    return 1;
                }
                return 0;
            }).forEach(entry -> {
                boolean hasAchievement = AchievementManager.hasAchievement(player.getName(), achievementCategoryData.id, entry.getKey());
                String achievementName = entry.getValue().getDisplayName();
                if (hasAchievement) {
                    simple.addButton(new ElementButton( achievementName + "\n" + TextFormat.RESET + UI_STATUS_HAS_ACHIEVEMENT));
                } else {
                    simple.addButton(new ElementButton( achievementName + "\n" + TextFormat.RESET + UI_STATUS_UNCLAIMED_ACHIEVEMENT));
                }
            });
        }
        simple.showToPlayer(player);
    }
}
