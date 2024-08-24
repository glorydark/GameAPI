package gameapi.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;

/**
 * @author glorydark
 */
public class TextEntityData {

    public static final String TYPE_NORMAL = "TextEntity";
    public static final String TYPE_RANKING = "RankingEntity";

    private Entity entity;

    private Position position;

    private String defaultText;

    public TextEntityData(Entity entity, Position position, String defaultText) {
        this.entity = entity;
        this.position = position;
        this.defaultText = defaultText;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Entity getEntity() {
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
}
