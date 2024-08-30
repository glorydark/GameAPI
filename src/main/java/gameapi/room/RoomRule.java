package gameapi.room;

import gameapi.room.utils.HideType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 */
@Data
public class RoomRule {

    private boolean allowBreakBlock = true;

    private boolean allowPlaceBlock = true;

    private int gameMode;

    private boolean allowReadyStartWalk = true;

    private boolean allowDropItem = true;

    private List<String> allowBreakBlocks = new ArrayList<>();

    private List<String> allowPlaceBlocks = new ArrayList<>();

    private boolean allowDamagePlayer = true;

    private boolean allowExplosion = true;

    private boolean allowEntityExplosionDamage = true;

    private boolean allowBlockExplosionDamage = true;

    private boolean allowExplosionBreakBlock = true;

    private boolean allowMagicDamage = true;

    private boolean allowFireDamage = true;

    private boolean allowHungerDamage = true;

    private boolean allowDrowningDamage = true;

    private boolean allowLightningDamage = true;

    private boolean allowFallDamage = true;

    private boolean allowProjectTileDamage = true;

    private boolean allowSuffocationDamage = true;

    private float defaultHealth = 20;

    private boolean allowFoodLevelChange = true;

    private boolean allowRespawn = true;

    private boolean allowCraft = true;

    private int respawnCoolDownTick = 20;

    private boolean noTimeLimit = false;

    private boolean allowSpectators = true;

    private boolean useDefaultAttackCooldown = true;

    private long attackCoolDownMillis = 0;

    private long playerReceiveEntityDamageCoolDownMillis = 500;

    private boolean experimentalFeature = false;

    private List<String> allowJoinPlayers = new ArrayList<>();

    private boolean virtualHealth = false;

    private boolean autoStartTeleport = true;

    private List<String> allowCommands = new ArrayList<>();

    private HideType hideType = HideType.NONE;

    private boolean allowJoinAfterStart = false;

    private boolean savePlayerPropertiesAfterQuit = false;

    private boolean autoAllocatePlayerToTeam = true;

    private int spectatorGameMode = 3;

    private boolean allowQuitByTeleport = true;

    public RoomRule(Integer gameMode) {
        this.gameMode = gameMode;
    }
}
