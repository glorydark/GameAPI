package gameapi.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.potion.Effect;
import gameapi.GameAPI;
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

    public EasyEffect() {

    }

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

    public static EasyEffect fromMap(Map<String, Object> map) {
        EasyEffect effect = new EasyEffect(1, 0, 0);
        effect.id = (Integer) map.get("id");
        effect.amplifier = (Integer) map.get("amplifier");
        effect.duration = (Integer) map.get("duration");
        if (map.containsKey("name")) {
            effect.name = (String) map.get("name");
        }
        if (map.containsKey("bad")) {
            effect.bad = (boolean) map.get("bad");
        }
        if (map.containsKey("visible")) {
            effect.bad = (boolean) map.get("visible");
        }
        if (map.containsKey("ambient")) {
            effect.bad = (boolean) map.get("ambient");
        }
        if (map.containsKey("color")) {
            List<Integer> rgbs = (List<Integer>) map.get("color");
            effect.rgb[0] = rgbs.get(0);
            effect.rgb[1] = rgbs.get(1);
            effect.rgb[2] = rgbs.get(2);
        }
        return effect;
    }

    public void giveEffect(Entity entity) {
        if (id == -1) {
            return;
        }
        Effect effect = Effect.getEffect(id);
        if (effect != null) {
            effect.setDuration(duration);
            effect.setAmplifier(amplifier);
            effect.setVisible(visible);
            if (rgb != null) {
                effect.setColor(rgb[0], rgb[1], rgb[2]);
            }
            effect.setAmbient(ambient);
            entity.addEffect(effect);
        } else {
            GameAPI.getInstance().getLogger().error("Error in parsing effect: " + this);
        }
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
