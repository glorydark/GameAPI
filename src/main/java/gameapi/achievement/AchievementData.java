package gameapi.achievement;

import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AchievementData {

    private String category;

    private String id;

    private String displayName;

    private String description;

    private String iconPathType;

    private String iconPath;

    public AchievementData(String category, String id, String displayName, String description, String iconPathType, String iconPath) {
        this.category = category;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.iconPathType = iconPathType;
        this.iconPath = iconPath;
    }
}
