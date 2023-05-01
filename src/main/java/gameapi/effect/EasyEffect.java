package gameapi.effect;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
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

    boolean bad = false;

    boolean visible = true;

    boolean ambient = false;

    String name = "";

    int[] rgb = new int[3];

    int version = 1;

    public EasyEffect(int id, int amplifier, int duration){
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
        this.name = Effect.getEffect(id).getName();
    }

    public void giveEffect(Player player){
        Effect effect = Effect.getEffect(id);
        if(version == 1) {
            effect.setDuration(duration);
            effect.setAmplifier(amplifier);
        } else if(version == 2) {
            effect.setColor(rgb[0], rgb[1], rgb[2]);
            effect.setVisible(visible);
            effect.setAmplifier(amplifier);
            effect.setDuration(duration);
            effect.setAmbient(ambient);
        }
        player.addEffect(effect);
    }

    public EasyEffect(Map<String, Object> map){
        this.id = (Integer) map.get("id");
        this.amplifier = (Integer) map.get("amplifier");
        this.duration = (Integer) map.get("duration");
        if(map.containsKey("name")){
            this.name = (String) map.get("name");
        }
        if(map.containsKey("bad")){
            this.bad = (boolean) map.get("bad");
        }
        if(map.containsKey("visible")){
            this.bad = (boolean) map.get("visible");
        }
        if(map.containsKey("ambient")){
            this.bad = (boolean) map.get("ambient");
        }
        if(map.containsKey("color")){
            List<Integer> rgbs = (List<Integer>)map.get("color");
            rgb[0] = rgbs.get(0);
            rgb[1] = rgbs.get(1);
            rgb[2] = rgbs.get(2);
        }
        this.version = 2;
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
                ", version=" + version +
                '}';
    }
}
