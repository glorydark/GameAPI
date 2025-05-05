package gameapi.room;

import gameapi.room.utils.BasicAttackSetting;
import gameapi.tools.type.TipElementType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Glorydark
 */
@Data
public class RoomRule {

    private boolean allowBreakBlock = true;

    private boolean allowPlaceBlock = true;

    private int gameMode;

    /**
     * 使用此选项时，teleport也会被禁止！
     * To set this to true, any teleportation will be rejected.
     */
    private boolean allowWalk = true;

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

    /**
     * 原版攻击冷却
     * Vanilla attack cooldown
     */
    private boolean useDefaultAttackCooldown = true;

    /**
     * 自定义攻击冷却（需要useDefaultAttackCooldown = false）
     * Custom attack cooldown (useDefaultAttackCooldown should be false)
     */
    private long attackCoolDownMillis = 0;

    private long playerReceiveEntityDamageCoolDownMillis = 500;

    private boolean experimentalFeature = false;

    private List<String> allowJoinPlayers = new ArrayList<>();

    private boolean virtualHealth = false;

    private boolean autoStartTeleport = true;

    private List<String> allowCommands = new ArrayList<>();

    private boolean autoAllocatePlayerToTeam = true;

    private boolean allowQuitByTeleport = true;

    private HideType hideType = HideType.NONE;

    private int spectatorGameMode = 3;

    private boolean allowJoinAfterReadyStart = false;

    private boolean allowJoinAfterStart = false;

    /**
     * todo: auto-rejoin system
     */
    private boolean savePlayerPropertiesAfterQuit = false;

    /**
     * 此选项用于单人测试多人逻辑，避免房间进入游戏后立马关闭，配合最低人数为1一起使用。
     * This option is used for multi-player performance testing while only you are participated in.
     * After enabling this, you should set the min player to 1 in order to start the test game.
     */
    private boolean testStatus = false;

    /**
     * PVP相关的kb设置
     * Set the settings for PVP servers
     */
    private BasicAttackSetting basicAttackSetting = null;

    private boolean protectMapBlock = false;

    /**
     * 末影珍珠伤害有时很迷惑，默认为关闭。
     * It is tricky to deal with damages caused by ender pearls. So this is default set to false.
     */
    private boolean allowEnderPearlDamage = false;

    private List<String> allowBreakProtectedMapBlocks = new ArrayList<>();

    /**
     * 暂时暂停原版音乐
     * Stop the game background music temporarily.
     */
    private boolean vanillaCustomMusic = true;

    /**
     * 是否进行颁奖典礼
     * Decide whether ceremony phase will exist or not.
     */
    private boolean hasCeremony = true;

    private boolean autoDestroyWhenBelowMinPlayers = false;

    protected static final Set<TipElementType> DEFAULT_HIDE_ELEMENT_TYPE = new HashSet<TipElementType>() {
        {
            this.add(TipElementType.BOSS_BAR);
            this.add(TipElementType.BROADCAST);
            this.add(TipElementType.CHAT);
            this.add(TipElementType.NAMETAG);
            this.add(TipElementType.SCOREBOARD);
            this.add(TipElementType.TIP);
        }
    };

    private Set<TipElementType> tipHideElements;

    private boolean enableRoomChatSystem = true;

    private boolean enableVanillaMoveCheck = true;

    private boolean defaultTitleOnStart = true;

    private boolean allowAttackPlayerBeforeStart = false;

    private boolean allowAttackEntityBeforeStart = false;

    public RoomRule(Integer gameMode) {
        this.gameMode = gameMode;
        this.tipHideElements = new HashSet<>(DEFAULT_HIDE_ELEMENT_TYPE);
    }
}
