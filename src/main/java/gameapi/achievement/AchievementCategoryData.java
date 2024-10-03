package gameapi.achievement;

import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AchievementCategoryData {

    public String id;

    public String displayName;

    public AchievementCategoryData(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
}
