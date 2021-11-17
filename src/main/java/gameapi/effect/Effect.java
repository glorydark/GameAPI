package gameapi.effect;

import cn.nukkit.Player;
import cn.nukkit.potion.Potion;

public class Effect {
    Integer id;
    Integer level;
    Integer duration;
    public Effect(int id,int level,int duration){
        this.id = id;
        this.level = level;
        this.duration = duration;
    }

    public static cn.nukkit.potion.Effect parseEffect(Player player, Effect effect){
        cn.nukkit.potion.Effect effectParse = cn.nukkit.potion.Effect.getEffect(effect.id);
        effectParse.setDuration(effect.duration*20);
        effectParse.setAmplifier(effect.level);
        return effectParse;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getDuration() {
        return duration;
    }
}
