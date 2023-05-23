package gameapi.room;

import gameapi.annotation.Experimental;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 */
public class RoomRule {
    public boolean allowBreakBlock = false;
    public boolean allowPlaceBlock = false;
    public int gameMode;
    public boolean readyStartWalk = true;
    public boolean noDropItem = true;
    public List<String> canBreakBlocks = new ArrayList<>();
    public List<String> canPlaceBlocks = new ArrayList<>();
    public boolean allowDamagePlayer = false;
    public boolean noTimeLimit = false;
    public boolean antiExplosion = true;
    public boolean allowEntityExplosionDamage = false;
    public boolean allowBlockExplosionDamage = false;
    public boolean allowMagicDamage = false;
    public boolean allowFireDamage = false;
    public boolean allowHungerDamage = false;
    public boolean allowDrowningDamage = false;
    public boolean allowLightningDamage = false;
    public boolean allowFallDamage = false;
    public boolean allowProjectTileDamage = false;
    public boolean allowSuffocationDamage = false;
    public float defaultHealth = 20;
    public boolean allowFoodLevelChange = true;
    public boolean allowRespawn = false;
    public int respawnCoolDownTick = 20;
    public int spectatorGameMode = 3;
    @Experimental public boolean needPreStartPass = false;
    @Experimental public boolean personal = false;

    public RoomRule(Integer gameMode){
        this.gameMode = gameMode;
    }

    @Override
    public String toString() {
        return "RoomRule{" +
                "allowBreakBlock=" + allowBreakBlock +
                ", allowPlaceBlock=" + allowPlaceBlock +
                ", gameMode=" + gameMode +
                ", noStartWalk=" + readyStartWalk +
                ", noDropItem=" + noDropItem +
                ", canBreakBlocks=" + canBreakBlocks +
                ", canPlaceBlocks=" + canPlaceBlocks +
                ", allowDamagePlayer=" + allowDamagePlayer +
                ", noTimeLimit=" + noTimeLimit +
                ", antiExplosion=" + antiExplosion +
                ", allowEntityExplosionDamage=" + allowEntityExplosionDamage +
                ", allowBlockExplosionDamage=" + allowBlockExplosionDamage +
                ", allowMagicDamage=" + allowMagicDamage +
                ", allowFireDamage=" + allowFireDamage +
                ", allowHungerDamage=" + allowHungerDamage +
                ", allowDrowningDamage=" + allowDrowningDamage +
                ", allowLightningDamage=" + allowLightningDamage +
                ", allowFallDamage=" + allowFallDamage +
                ", allowProjectTileDamage=" + allowProjectTileDamage +
                ", allowSuffocationDamage=" + allowSuffocationDamage +
                ", defaultHealth=" + defaultHealth +
                ", allowFoodLevelChange=" + allowFoodLevelChange +
                ", allowRespawn=" + allowRespawn +
                ", respawnCoolDownTick=" + respawnCoolDownTick +
                ", allowSpectatorMode=" + spectatorGameMode +
                ", needPreStartPass=" + needPreStartPass +
                '}';
    }
}
