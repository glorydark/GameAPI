package gameapi.entity.data;

import cn.nukkit.level.Position;
import gameapi.entity.TextEntity;

/**
 * @author glorydark
 */
public class TextEntityData {

    public static final String TYPE_NORMAL = "TextEntity";
    public static final String TYPE_RANKING = "RankingEntity";

    private TextEntity entity;

    private Position position;

    private String defaultText;

    private final long startMillis;

    public TextEntityData(TextEntity entity, Position position, String defaultText) {
        this.entity = entity;
        this.position = position;
        this.defaultText = defaultText;
        this.startMillis = System.currentTimeMillis();
    }

    public void setEntity(TextEntity entity) {
        this.entity = entity;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public TextEntity getEntity() {
        return entity;
    }

    public Position getPosition() {
        return position;
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
