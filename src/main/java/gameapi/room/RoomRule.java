package gameapi.room;

import java.util.ArrayList;
import java.util.List;

public class RoomRule {
    public Boolean allowBreakBlock = false;
    public Boolean allowPlaceBlock = false;
    public int gamemode;
    public Boolean startNoWalk = true;
    public Boolean noDropItem = true;
    public List<Integer> canBreakBlocks = new ArrayList<>();
    public List<Integer> canPlaceBlocks = new ArrayList<>();
    public Boolean allowDamagePlayer = false;
    public Boolean noDropDamage = true;
    public Boolean noTimeLimit = false;
    public Boolean antiExplosion = true;
    public Boolean allowEntityExplosionDamage = false;
    public Boolean allowBlockExplosionDamage = false;
    public Boolean allowMagicDamage = false;
    public Boolean allowFireDamage = false;
    public Boolean allowHungerDamage = false;
    public Boolean allowDrowningDamage = false;
    public Boolean allowLightningDamage = false;
    public Boolean allowFallDamage = false;
    public Boolean allowProjectTileDamage = false;
    public Boolean allowSuffocationDamage = false;
    public float defaultHealth = 20;


    public RoomRule(Integer gamemode){
        this.gamemode = gamemode;
    }
}
