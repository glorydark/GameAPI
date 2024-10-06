package gameapi.achievement;

import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AchievementCategoryData {

    public String id;

    public String displayName;

    private String iconPathType;

    private String iconPath;

    public AchievementCategoryData(String id, String displayName, String iconPathType, String iconPath) {
        this.id = id;
        this.displayName = displayName;
        this.iconPathType = iconPathType;
        this.iconPath = iconPath;
    }
}
