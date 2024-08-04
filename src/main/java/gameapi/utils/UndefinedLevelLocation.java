package gameapi.utils;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import lombok.Data;

/**
 * @author glorydark
 * 快捷创建未指定世界的位置变量
 */
@Data
public class UndefinedLevelLocation {

    private Vector3 pos = null;

    private Vector3 rotation = null;

    public UndefinedLevelLocation() {

    }

    public UndefinedLevelLocation(Vector3 pos) {
        this(pos, null);
    }

    public UndefinedLevelLocation(Vector3 pos, Vector3 rotation) {
        this.pos = pos;
        this.rotation = rotation;
    }

    public AdvancedLocation toAdvancedLocation(Level level) {
        if (this.rotation != null) {
            return new AdvancedLocation(this.pos, this.rotation, level);
        } else {
            return new AdvancedLocation(this.pos, level);
        }
    }
}
