package gameapi.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.potion.Effect;
import gameapi.tools.SmartTools;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class EasyEffect {
    Integer id;

    Integer amplifier;

    Integer duration;

    boolean bad;

    boolean visible = true;

    boolean ambient = false;

    String name = "";

    int[] rgb = null;

    public EasyEffect(int id, int amplifier, int duration, boolean bad, boolean visible, boolean ambient, int r, int g, int b) {
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
        this.name = Effect.getEffect(id).getName();
        this.bad = bad;
        this.visible = visible;
        this.ambient = ambient;
        if (SmartTools.isInRange(0, 255, r) && SmartTools.isInRange(0, 255, g) && SmartTools.isInRange(0, 255, b)) {
            this.rgb = new int[]{r, g, b};
        }
    }

    public EasyEffect(int id, int amplifier, int duration) {
        this(id, amplifier, duration, false, false, false, -1, -1, -1);
    }

    public EasyEffect(Map<String, Object> map) {
        this.id = (Integer) map.get("id");
        this.amplifier = (Integer) map.get("amplifier");
        this.duration = (Integer) map.get("duration");
        if (map.containsKey("name")) {
            this.name = (String) map.get("name");
        }
        if (map.containsKey("bad")) {
            this.bad = (boolean) map.get("bad");
        }
        if (map.containsKey("visible")) {
            this.bad = (boolean) map.get("visible");
        }
        if (map.containsKey("ambient")) {
            this.bad = (boolean) map.get("ambient");
        }
        if (map.containsKey("color")) {
            List<Integer> rgbs = (List<Integer>) map.get("color");
            rgb[0] = rgbs.get(0);
            rgb[1] = rgbs.get(1);
            rgb[2] = rgbs.get(2);
        }
    }

    public void giveEffect(Entity entity) {
        Effect effect = Effect.getEffect(id);
        effect.setDuration(duration);
        effect.setAmplifier(amplifier);
        effect.setVisible(visible);
        if (rgb != null) {
            effect.setColor(rgb[0], rgb[1], rgb[2]);
        }
        effect.setAmbient(ambient);
        entity.addEffect(effect);
    }

    @Override
    public String toString() {
        return "EasyEffect{" +
                "id=" + id +
                ", amplifier=" + amplifier +
                ", duration=" + duration +
                ", bad=" + bad +
                ", visible=" + visible +
                ", ambient=" + ambient +
                ", name='" + name + '\'' +
                ", rgb=" + Arrays.toString(rgb) +
                '}';
    }
}
