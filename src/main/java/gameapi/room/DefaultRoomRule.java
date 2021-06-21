package gameapi.room;

import cn.nukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class DefaultRoomRule {
    public Boolean allowRespawn;
    public Boolean allowBreakBlock = false;
    public Boolean allowPlaceBlock = false;
    public int gamemode;
    public Boolean startNoWalk = true;
    public Boolean noDropItem = true;
    public int spawnCoolDown;
    public List<Integer> canBreakBlocks = new ArrayList<>();
    public List<Integer> canPlaceBlocks = new ArrayList<>();
    public Boolean allowTeamDamage = false;
    public Boolean allowDamagePlayer = false;
    public Boolean noDropDamage = true;

    public DefaultRoomRule(Integer gamemode, Boolean allowRespawn, Integer spawnCoolDown){
        this.allowRespawn = allowRespawn;
        this.gamemode = gamemode;
        this.spawnCoolDown = spawnCoolDown;
    }
}
