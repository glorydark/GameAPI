package gameapi.entity.data;

import cn.nukkit.level.Location;
import gameapi.entity.TextEntity;

/**
 * @author glorydark
 */
public class TextEntityData {

    public static final String TYPE_NORMAL = "TextEntity";
    public static final String TYPE_RANKING = "RankingEntity";

    private TextEntity entity;

    private Location location;

    private String defaultText;

    private final long startMillis;

    public TextEntityData(TextEntity entity, Location location, String defaultText) {
        this.entity = entity;
        this.location = location;
        this.defaultText = defaultText;
        this.startMillis = System.currentTimeMillis();
    }

    public void setEntity(TextEntity entity) {
        this.entity = entity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TextEntity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    public String getEntityType() {
        return TYPE_NORMAL;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public long getStartMillis() {
        return startMillis;
    }
}
