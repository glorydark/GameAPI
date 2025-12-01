package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.CreativeContentPacket;
import gameapi.GameAPI;
import gameapi.commands.defaults.dev.HideChatCommand;
import gameapi.entity.TextEntity;
import gameapi.event.player.*;
import gameapi.event.room.*;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.extensions.supplyChest.SupplyChest;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.form.element.ResponsiveElementInput;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.music.NBSMusicManager;
import gameapi.manager.music.OggMusicManager;
import gameapi.manager.room.AdvancedBlockManager;
import gameapi.manager.room.CheckpointManager;
import gameapi.manager.room.GhostyManager;
import gameapi.manager.room.RoomVirtualHealthManager;
import gameapi.manager.tools.ScoreboardManager;
import gameapi.room.executor.BaseRoomExecutor;
import gameapi.room.executor.RoomExecutor;
import gameapi.room.items.RoomItemBase;
import gameapi.room.state.StageState;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.team.BaseTeam;
import gameapi.room.utils.reason.JoinRoomReason;
import gameapi.room.utils.reason.QuitRoomReason;
import gameapi.room.utils.reason.ResetAllReason;
import gameapi.tools.PlayerTools;
import gameapi.tools.TipsTools;
import gameapi.tools.WorldTools;
import gameapi.tools.type.TipElementType;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.EntityDamageSource;
import gameapi.utils.TitleData;
import gameapi.utils.text.GameTextContainer;
import it.unimi.dsi.fastutil.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class Room {

    public List<CustomRoomStatus> roomStatusList = RoomDefaultStatusFactory.DEFAULT_ROOM_STATUS_LIST;
    public static final String INTERNAL_KEY_ROOM_INTERNAL = "room_internal";
    public static final String INTERNAL_KEY_VISIBLE_TO_PLAYERS = "visible_to_players";
    public static final BiFunction<Room, Player, Boolean> DEFAULT_ROOM_ADD_PLAYER_CHECK = (room, player) -> {
        switch (room.getCurrentRoomStatus().getIdentifier()) {
            case RoomDefaultStatusFactory.ROOM_MAP_LOAD_FAILED_ID:
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.load_failed"));
                return true;
            case RoomDefaultStatusFactory.ROOM_MAP_INITIALIZING_ID:
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.resetting"));
                return true;
            case RoomDefaultStatusFactory.ROOM_HALTED_ID:
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.halted"));
                return true;
            case RoomDefaultStatusFactory.ROOM_STATUS_READY_START_ID:
                if (room.getRoomRule().isAllowJoinAfterReadyStart()) {
                    break;
                } else {
                    room.processSpectatorJoin(player);
                }
                return true;
            case RoomDefaultStatusFactory.ROOM_STATUS_GAME_START_ID:
            case RoomDefaultStatusFactory.ROOM_STATUS_NEXT_ROUND_PRESTART_ID:
                if (!room.getRoomRule().isAllowJoinAfterStart()) {
                    if (room.getRoomRule().isAllowSpectators()) {
                        room.processSpectatorJoin(player);
                        return true;
                    } else {
                        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.started"));
                    }
                } else if (room.getRoomRule().isAllowSpectators()) {
                    room.processSpectatorJoin(player);
                    return true;
                }
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_GAME_END_ID:
            case RoomDefaultStatusFactory.ROOM_STATUS_CEREMONY_ID:
            case RoomDefaultStatusFactory.ROOM_STATUS_ROOM_END_ID:
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.started"));
                return true;
        }
        return false;
    };
    private final long createMillis;
    public long maxTempRoomWaitMillis = 1800000L;
    @Setter(AccessLevel.NONE)
    protected ConcurrentHashMap<String, BaseTeam> teamCache = new ConcurrentHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, LinkedHashMap<String, Object>> playerProperties = new LinkedHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, Object> roomProperties = new LinkedHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, LinkedHashMap<String, Object>> internalProperties = new LinkedHashMap<>();
    protected String joinPassword = "";
    protected List<TextEntity> textEntities = new ArrayList<>();
    protected List<BlockVector3> blocks = new ArrayList<>();
    // If returned with true, it means this function is enough to do with the join process.
    // Other default logic will be defunct.
    protected BiFunction<Room, Player, Boolean> overridePlayerPreJoinProcess = DEFAULT_ROOM_ADD_PLAYER_CHECK;
    private int roomNumber = -1;
    // Used as a temporary room and will be deleted after the game.
    private RoomExecutor statusExecutor = new BaseRoomExecutor(this);
    private boolean temporary = false;
    @Deprecated
    private boolean autoDestroy = false;
    private boolean resetMap = true;
    private String roomName = "";
    private RoomRule roomRule;
    private CustomRoomStatus currentRoomStatus = RoomDefaultStatusFactory.ROOM_INITIALIZING;
    private List<Player> players = new ArrayList<>();
    private List<Player> spectators = new ArrayList<>();
    private int maxPlayer = 16;
    private int minPlayer = 1;
    private int waitTime = 10;
    private int gameWaitTime = 10;
    private int gameTime = 10;
    private int ceremonyTime = 10;
    private int gameEndTime = 3;
    private int nextRoundPreStartTime = 10;
    private int maxRound;
    private int round = 0;
    private int gameDuration = 0;
    private int time = 0; // Spent Seconds
    private boolean isAllowedToStart = true;
    private List<Level> playLevels = new ArrayList<>();
    private AdvancedLocation waitSpawn = new AdvancedLocation();
    private List<AdvancedLocation> startSpawn = new ArrayList<>();
    private AdvancedLocation endSpawn = new AdvancedLocation();
    private List<AdvancedLocation> spectatorSpawn = new ArrayList<>();
    private String roomLevelBackup;
    private String gameName;
    private List<String> winConsoleCommands = new ArrayList<>();
    private List<String> loseConsoleCommands = new ArrayList<>();
    // Save data of room's chat history.
    private List<RoomChatData> chatDataList = new ArrayList<>();
    private long startMillis;
    private String tempWorldPrefixOverride = "";
    private int id = -1;
    private List<StageState> stageStates = new ArrayList<>();
    private List<SupplyChest> supplyChests = new ArrayList<>();
    private List<DynamicObstacle> dynamicObstacles = new ArrayList<>();
    private Map<String, RoomItemBase> roomItems = new LinkedHashMap<>();
    private ScheduledExecutorService roomTaskExecutor = Executors.newScheduledThreadPool(4);
    private RoomUpdateTask roomUpdateTask;
    private CheckpointManager checkpointManager;
    private RoomVirtualHealthManager roomVirtualHealthManager = new RoomVirtualHealthManager(this);
    private AdvancedBlockManager advancedBlockManager = new AdvancedBlockManager();
    private GhostyManager ghostyManager = new GhostyManager(this);
    private NBSMusicManager nbsMusicManager;
    private OggMusicManager oggMusicManager;
    private boolean autoDestroyOverTime = true; // 超过maxWaitMillis自动释放房间
    private List<String> roomAdmins = new ArrayList<>();
    private String creator = "";
    private List<String> whitelists = new ArrayList<>();
    private boolean enableWhitelist = false;
    private int accelerateWaitCountDownPlayerCount = 2;
    private Map<Entity, List<EntityDamageSource>> lastEntityReceiveDamageSource = new LinkedHashMap<>();
    private String prevChangeStatusReason = "";

    public Room(String gameName, RoomRule roomRule, int round) {
        this(gameName, roomRule, "", round);
    }

    public Room(String gameName, RoomRule roomRule, String roomLevelBackup, int round) {
        this.maxRound = round;
        this.roomRule = roomRule;
        this.gameName = gameName;
        this.roomUpdateTask = new RoomUpdateTask(this);
        this.roomLevelBackup = roomLevelBackup;
        this.createMillis = System.currentTimeMillis();
        this.checkpointManager = new CheckpointManager(this);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public List<Player> getSpectators() {
        return new ArrayList<>(this.spectators);
    }

    public List<Player> getPlayersWithoutCreate() {
        return this.players;
    }

    public List<Player> getSpectatorsWithoutCreate() {
        return this.spectators;
    }


    public void registerRoomItem(RoomItemBase... roomItems) {
        for (RoomItemBase roomItem : roomItems) {
            this.roomItems.put(roomItem.getIdentifier(), roomItem);
        }
    }

    public RoomItemBase getRoomItem(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        return this.roomItems.get(identifier);
    }

    public Object getPlayerProperty(Player player, String key) {
        return this.getPlayerProperty(player.getName(), key);
    }

    public <T> T getPlayerProperty(Player player, String key, T defaultValue) {
        return this.getPlayerProperty(player.getName(), key, defaultValue);
    }

    public void setPlayerProperty(Player player, String key, Object value) {
        this.setPlayerProperty(player.getName(), key, value);
    }

    public Object getPlayerProperty(String player, String key) {
        return this.getPlayerProperty(player, key, null);
    }

    public <T> T getPlayerProperty(String player, String key, T defaultValue) {
        return this.playerProperties.containsKey(player) ? (T) this.playerProperties.get(player).getOrDefault(key, defaultValue) : defaultValue;
    }

    public void setPlayerProperty(String player, String key, Object value) {
        this.playerProperties.computeIfAbsent(player, (Function<String, LinkedHashMap<String, Object>>) o -> new LinkedHashMap<>()).put(key, value);
    }

    public boolean hasPlayerProperty(Player player, String key) {
        return this.hasPlayerProperty(player.getName(), key);
    }

    public boolean hasPlayerProperty(String player, String key) {
        if (this.playerProperties.containsKey(player)) {
            return this.playerProperties.get(player).containsKey(key);
        } else {
            return false;
        }
    }

    public void removePlayerProperty(Player player, String key) {
        this.removePlayerProperty(player.getName(), key);
    }

    public void removePlayerProperty(String player, String key) {
        if (this.playerProperties.containsKey(player)) {
            this.playerProperties.get(player).remove(key);
        }
    }

    public Object getRoomProperty(String key) {
        return this.getRoomProperty(key, null);
    }

    public <T> T getRoomProperty(String key, T defaultValue) {
        return (T) roomProperties.getOrDefault(key, defaultValue);
    }

    public void setRoomProperty(String key, Object value) {
        this.roomProperties.put(key, value);
    }

    public boolean hasRoomProperty(String key) {
        return this.roomProperties.containsKey(key);
    }

    //
    public Object getInternalPlayerProperty(Player player, String key) {
        return this.getInternalPlayerProperty(player.getName(), key);
    }

    public <T> T getInternalPlayerProperty(Player player, String key, T defaultValue) {
        return this.getInternalPlayerProperty(player.getName(), key, defaultValue);
    }

    public void setInternalPlayerProperty(Player player, String key, Object value) {
        this.setInternalPlayerProperty(player.getName(), key, value);
    }

    public Object getInternalPlayerProperty(String player, String key) {
        return this.getInternalPlayerProperty(player, key, null);
    }

    public <T> T getInternalPlayerProperty(String player, String key, T defaultValue) {
        return this.internalProperties.containsKey(player) ? (T) this.internalProperties.get(player).getOrDefault(key, defaultValue) : defaultValue;
    }

    public void setInternalPlayerProperty(String player, String key, Object value) {
        this.internalProperties.computeIfAbsent(player, (Function<String, LinkedHashMap<String, Object>>) o -> new LinkedHashMap<>()).put(key, value);
    }

    public boolean hasInternalPlayerProperty(String player, String key) {
        if (this.internalProperties.containsKey(player)) {
            return this.internalProperties.get(player).containsKey(key);
        } else {
            return false;
        }
    }

    public Object getInternalRoomProperty(String key) {
        return this.getRoomProperty(key, null);
    }

    public <T> T getInternalRoomProperty(String key, T defaultValue) {
        return getInternalPlayerProperty(INTERNAL_KEY_ROOM_INTERNAL, key, defaultValue);
    }

    public void setInternalRoomProperty(String key, Object value) {
        this.setInternalPlayerProperty(INTERNAL_KEY_ROOM_INTERNAL, key, value);
    }

    public boolean hasInternalRoomProperty(String key) {
        return hasInternalPlayerProperty(INTERNAL_KEY_ROOM_INTERNAL, key);
    }

    public void executeLoseCommands(Player player) {
        for (String string : this.loseConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(),
                    string.replace("{player}", "\"" + player.getName() + "\"")
                            .replace("{level}", player.getLevel().getName())
                            .replace("{game_name}", this.gameName)
                            .replace("{room_name}", this.roomName));
        }
    }

    public void executeWinCommands(Player player) {
        for (String string : this.winConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(),
                    string.replace("{player}", "\"" + player.getName() + "\"")
                            .replace("{level}", player.getLevel().getName())
                            .replace("{game_name}", this.gameName)
                            .replace("{room_name}", this.roomName));
        }
    }

    public void allocatePlayerToTeams() {
        this.allocatePlayerToTeams(true);
    }

    public void allocatePlayerToTeams(boolean balance) {
        if (this.teamCache.keySet().isEmpty()) {
            return;
        }
        List<Player> allPlayers = this.getPlayers();
        Collections.shuffle(allPlayers);
        if (balance) {
            List<BaseTeam> list = new ArrayList<>(this.getTeams())
                    .stream()
                    .filter(BaseTeam::isAvailable)
                    .sorted(Comparator.comparing(BaseTeam::getSize))
                    .collect(Collectors.toList());
            for (Player player : allPlayers) {
                if (this.getTeam(player) != null) {
                    continue;
                }
                boolean hasResult = false;
                for (BaseTeam team : list) {
                    if (team.addPlayer(player)) {
                        list = new ArrayList<>(this.getTeams())
                                .stream()
                                .filter(BaseTeam::isAvailable)
                                .sorted(Comparator.comparing(BaseTeam::getSize))
                                .toList();
                        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", team.getPrefix() + team.getRegistryName()));
                        hasResult = true;
                        break;
                    }
                }
                if (!hasResult) {
                    this.removePlayer(player, QuitRoomReason.ALLOCATE_ERROR);
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.team.no_available"));
                }
            }
        } else {
            for (Player player : allPlayers) {
                if (this.getTeam(player) != null) {
                    continue;
                }
                List<BaseTeam> baseTeams = new ArrayList<>(this.teamCache.values());
                Collections.shuffle(baseTeams);
                boolean hasResult = false;
                for (BaseTeam baseTeam : baseTeams) {
                    if (baseTeam.addPlayer(player, false)) {
                        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", baseTeam.getPrefix() + baseTeam.getRegistryName()));
                        hasResult = true;
                        break;
                    }
                }
                if (!hasResult) {
                    this.removePlayer(player, QuitRoomReason.ALLOCATE_ERROR);
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.team.no_available"));
                }
            }
        }
    }

    public boolean addTeamPlayer(String registry, Player player) {
        if (this.getTeam(player) != null) {
            return false;
        }
        BaseTeam team = this.teamCache.get(registry);
        if (team != null) { //禁止加入满人队伍
            if (team.addPlayer(player)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", team.getPrefix() + team.getRegistryName()));
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public void removePlayerFromTeam(Player player) {
        BaseTeam team = this.getTeam(player);
        if (team == null) {
            return;
        }
        team.removePlayer(player);
        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.quit", team.getPrefix() + team.getRegistryName()));
    }

    public BaseTeam getTeam(Player player) {
        for (Map.Entry<String, BaseTeam> entrySet : this.teamCache.entrySet()) {
            if (entrySet.getValue().hasPlayer(player)) {
                return entrySet.getValue();
            }
        }
        return null;
    }

    public List<BaseTeam> getTeams() {
        return new ArrayList<>(this.teamCache.values());
    }

    public List<BaseTeam> getAvailableTeams() {
        List<BaseTeam> baseTeams = new ArrayList<>(this.teamCache.values());
        baseTeams.removeIf(baseTeam -> baseTeam.getPlayers().isEmpty());
        return baseTeams;
    }

    public List<BaseTeam> getOpponentTeams(BaseTeam baseTeam) {
        List<BaseTeam> baseTeams = new ArrayList<>(this.getAvailableTeams());
        baseTeams.remove(baseTeam);
        return baseTeams;
    }

    public BaseTeam getTeam(String teamId) {
        return this.teamCache.getOrDefault(teamId, null);
    }

    public void registerTeam(BaseTeam team) {
        this.teamCache.put(team.getRegistryName(), team);
    }

    public void addPlayer(Player player) {
        this.addPlayer(player, JoinRoomReason.DEFAULT);
    }

    public void addPlayer(Player player, JoinRoomReason joinRoomReason) {
        this.addPlayer(player, null, joinRoomReason);
    }

    /*
        Here we genuinely add an authentication process,
        which aims to serve the server hosting some big events
     */
    public void addPlayer(Player player, String enterPassword, JoinRoomReason joinRoomReason) {
        if (this.enableWhitelist) {
            if (!this.whitelists.contains(player.getName())) {
                if (this.roomRule.isAllowSpectators()) {
                    this.processSpectatorJoin(player);
                } else {
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.whitelisted"));
                }
                return;
            }
        }
        if (!this.joinPassword.isEmpty()) {
            String rightPassword = this.getJoinPassword();
            if (enterPassword == null) {
                AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(GameAPI.getLanguage().getTranslation(player, "room.window.password.title"))
                        .input(
                                new ResponsiveElementInput(GameAPI.getLanguage().getTranslation(player, "room.window.password.input_text"))
                                        .onRespond((player1, s) -> {
                                            if (rightPassword.equals(s)) {
                                                processPlayerJoin(player1, joinRoomReason);
                                            } else {
                                                player1.sendMessage(GameAPI.getLanguage().getTranslation(player1, "room.password.wrong"));
                                            }
                                        })
                        );
                custom.showToPlayer(player);
            } else {
                if (rightPassword.equals(enterPassword)) {
                    processPlayerJoin(player, joinRoomReason);
                } else {
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.password.wrong"));
                }
            }
        } else {
            processPlayerJoin(player);
        }
    }

    public void processPlayerJoin(Player player) {
        this.processPlayerJoin(player, JoinRoomReason.DEFAULT);
    }

    public void processPlayerJoin(Player player, JoinRoomReason joinRoomReason) {
        Room oldRoom = RoomManager.getRoom(player);
        if (oldRoom != null) {
            if (oldRoom != this) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_other_room"));
                return;
            }
        }
        List<String> whitelists = this.getRoomRule().getAllowJoinPlayers();
        if (!whitelists.isEmpty()) {
            if (!whitelists.contains(player.getName())) {
                player.sendMessage(GameAPI.getLanguage().getTranslation("room.game.no_access"));
                return;
            }
        }

        if (this.overridePlayerPreJoinProcess.apply(this, player)) {
            return;
        }

        if (this.players.size() < this.maxPlayer) {
            if (this.hasPlayer(player) || this.hasSpectator(player)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_this_room"));
            } else {
                RoomManager.getPlayerRoomHashMap().put(player, this);
                RoomPlayerPreJoinEvent ev = new RoomPlayerPreJoinEvent(this, player);
                GameListenerRegistry.callEvent(this, ev);
                if (ev.isCancelled()) {
                    RoomManager.getPlayerRoomHashMap().remove(player);
                } else {
                    for (Player player1 : this.getPlayers()) {
                        if (this.getInternalPlayerProperty(player, INTERNAL_KEY_VISIBLE_TO_PLAYERS, false)) {
                            player.hidePlayer(player1);
                        }
                    }
                    this.roomUpdateTask.setPlayerLastLocation(player, player.getLocation());
                    this.playerProperties.computeIfAbsent(player.getName(), (Function<String, LinkedHashMap<String, Object>>) o -> new LinkedHashMap<>());
                    this.players.add(player);
                    this.waitSpawn.teleport(player);
                    player.setGamemode(2);
                    player.getFoodData().reset();
                    player.setAllowFlight(false);
                    this.resetSpeed(player);
                    player.setFoodEnabled(this.getRoomRule().isAllowFoodLevelChange());
                    player.setHealth(player.getMaxHealth());
                    if (!this.getRoomRule().isEnableVanillaMoveCheck()) {
                        player.setCheckMovement(false);
                    }
                    if (GameAPI.getInstance().isTipsEnabled()) {
                        for (Level playLevel : this.getPlayLevels()) {
                            TipsTools.closeTipsShow(playLevel.getName(), player, this.getRoomRule().getTipHideElements().toArray(new TipElementType[0]));
                        }
                    }
                    for (Player p : this.players) {
                        p.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.broadcast.join", player.getName(), this.players.size(), this.maxPlayer));
                    }
                    for (Player p : this.spectators) {
                        p.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.broadcast.join", player.getName(), this.players.size(), this.maxPlayer));
                    }
                    GameListenerRegistry.callEvent(this, new RoomPlayerJoinEvent(this, player));
                }
            }
        } else {
            if (!this.roomRule.isAllowSpectators()) {
                player.sendMessage(GameAPI.getLanguage().getTranslation("room.game.full"));
            }
        }
    }

    public void removePlayer(Player player) {
        this.removePlayer(player, QuitRoomReason.DEFAULT);
    }

    public void removePlayer(Player player, QuitRoomReason reason) {
        if (!this.getPlayers().contains(player)) {
            return;
        }
        RoomPlayerLeaveEvent ev = new RoomPlayerLeaveEvent(this, player, reason);
        GameListenerRegistry.callEvent(this, ev);
        if (!ev.isCancelled()) {
            if (!this.getRoomRule().isSavePlayerPropertiesAfterQuit()) {
                this.playerProperties.remove(player.getName());
            }
            for (Player p : this.getPlayers()) {
                p.sendMessage(GameAPI.getLanguage().getTranslation(p, "baseEvent.quit.success", player.getName()));
            }
            // reset hide status
            for (Player player1 : this.getPlayers()) {
                player1.showPlayer(player);
                player.showPlayer(player1);
            }
            if (GameAPI.getInstance().isTipsEnabled()) {
                for (Level playLevel : this.getPlayLevels()) {
                    TipsTools.removeTipsConfig(playLevel.getName(), player, this.getRoomRule().getTipHideElements().toArray(new TipElementType[0]));
                }
            }
            this.getGhostyManager().stopRecordingPlayer(player);
            player.getFoodData().reset();
            player.setFoodEnabled(true);
            this.resetSpeed(player);
            player.setHealth(player.getMaxHealth());
            player.setGamemode(Server.getInstance().getDefaultGamemode());
            this.showAllPlayers(player);
            this.roomVirtualHealthManager.removePlayer(player);
            this.removePlayerFromTeam(player);
            ScoreboardManager.removeScoreboard(player);
            if (this.getOggMusicManager() != null) {
                this.getOggMusicManager().onQuit(player);
            }
            this.players.remove(player);
            player.setCheckMovement(false);

            RoomManager.getPlayerRoomHashMap().remove(player);

            this.removeHideStatus(player);

            if (reason != QuitRoomReason.TELEPORT) {
                if (this.getEndSpawn() != null && this.getEndSpawn().getLocation() != null && this.getEndSpawn().getLocation().isValid()) {
                    this.getEndSpawn().teleport(player);
                } else {
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
            GameAPI.getGameDebugManager().info("Player " + player.getName() + " quit the room, reason: " + reason.name());

            RoomPlayerPostLeaveEvent roomPlayerPostLeaveEvent = new RoomPlayerPostLeaveEvent(this, player, reason);
            GameListenerRegistry.callEvent(this, roomPlayerPostLeaveEvent);
        }
    }

    @Deprecated
    public void setRoomStatus(@NotNull RoomStatus status) {
        this.setRoomStatus(status, "internal");
    }

    public void setRoomStatus(@NotNull RoomStatus status, String reason) {
        CustomRoomStatus customRoomStatus = RoomDefaultStatusFactory.getByIdentifier(status.getIdentifier());
        if (customRoomStatus == null) {
            GameAPI.getGameDebugManager().error("Error in unknown RoomStatus: " + status.getIdentifier());
            return;
        }
        this.setCurrentRoomStatus(customRoomStatus, reason);
    }

    public CustomRoomStatus getCurrentRoomStatus() {
        return this.currentRoomStatus;
    }

    @Deprecated
    public @Nullable RoomStatus getRoomStatus() {
        String id = this.currentRoomStatus.getIdentifier();
        for (RoomStatus value : RoomStatus.values()) {
            if (value.getIdentifier().equals(id)) {
                return value;
            }
        }
        return null;
    }

    public void setCurrentRoomStatus(@NotNull CustomRoomStatus status) {
        setCurrentRoomStatus(status, "internal");
    }

    public void setCurrentRoomStatus(@NotNull CustomRoomStatus status, String reason) {
        final CustomRoomStatus prevStatus = this.getCurrentRoomStatus();
        String prevReason = this.prevChangeStatusReason;
        this.prevChangeStatusReason = reason;
        if (status == this.currentRoomStatus) {
            GameAPI.getGameDebugManager().warning("Found duplicated move in setting the same status for a room, room: " + this.getRoomName() + ", reason: " + reason + ", lastReason: " + prevReason + ", status: " + this.currentRoomStatus);
            return;
        }
        this.time = 0;
        switch (status.getIdentifier()) {
            case RoomDefaultStatusFactory.ROOM_STATUS_PRESTART_ID:
                GameListenerRegistry.callEvent(this, new RoomPreStartEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_READY_START_ID:
                GameListenerRegistry.callEvent(this, new RoomReadyStartEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_GAME_START_ID:
                GameListenerRegistry.callEvent(this, new RoomGameStartEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_GAME_END_ID:
                GameListenerRegistry.callEvent(this, new RoomGameEndEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_CEREMONY_ID:
                GameListenerRegistry.callEvent(this, new RoomCeremonyEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_NEXT_ROUND_PRESTART_ID:
                GameListenerRegistry.callEvent(this, new RoomNextRoundPreStartEvent(this));
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_ROOM_END_ID:
                GameListenerRegistry.callEvent(this, new RoomEndEvent(this));
                break;
            default:
                GameListenerRegistry.callEvent(this, new RoomCustomStatusChangeEvent(this, prevStatus, status));
                break;
        }
        this.currentRoomStatus = status;
    }

    public void resetAll() {
        resetAll(ResetAllReason.DEFAULT);
    }

    public void resetAll(ResetAllReason resetAllReason) {
        if (this.currentRoomStatus == RoomDefaultStatusFactory.ROOM_MAP_INITIALIZING) {
            return;
        }
        this.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_MAP_INITIALIZING, "internal");
        if (this.getRoomTaskExecutor() != null) {
            this.getRoomUpdateTask().cancel();
            this.getRoomTaskExecutor().shutdownNow();
            GameAPI.getGameDebugManager().info("关闭线程池成功: " + this.getRoomTaskExecutor().toString());
            this.setRoomTaskExecutor(null);
        }
        this.stageStates = new ArrayList<>();
        GameListenerRegistry.callEvent(this, new RoomResetEvent(this));
        for (Player player : this.getSpectators()) {
            this.removeSpectator(player);
        }
        for (Player player : this.getPlayers()) {
            this.removePlayer(player, QuitRoomReason.ROOM_RESET);
        }
        //因为某些原因无法正常传送走玩家，就全部踹出服务器！
        this.getLastEntityReceiveDamageSource().clear();
        this.players = new ArrayList<>();
        this.round = 0;
        this.time = 0;
        this.playerProperties = new LinkedHashMap<>();
        this.teamCache.forEach((s, team) -> team.resetAll());
        this.chatDataList = new ArrayList<>();
        this.getCheckpointManager().clearAllPlayerCheckPointData();
        this.getGhostyManager().clearAll();
        this.roomVirtualHealthManager.clearAll();
        // 增加默认地图判断
        for (Level playLevel : this.playLevels) {
            if (playLevel == null || playLevel.getProvider() == null) {
                continue;
            }
            boolean b = this.getEndSpawn() != null && this.getEndSpawn().getLocation() != null && this.getEndSpawn().getLocation().isValid();
            if (playLevel != Server.getInstance().getDefaultLevel()) {
                for (Player player : playLevel.getPlayers().values()) {
                    if (b) {
                        this.getEndSpawn().teleport(player);
                    } else {
                        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
                for (Player player : playLevel.getPlayers().values()) {
                    player.kick("Teleport error!");
                }
            }
        }
        if (this.temporary) {
            this.getDynamicObstacles().clear();
            if (this.resetMap) {
                GameAPI.getInstance().getLogger().alert(GameAPI.getLanguage().getTranslation("room.detect_delete", this.getRoomName()));
                for (Level playLevel : this.playLevels) {
                    if (playLevel != null && playLevel.getProvider() != null) {
                        String levelName = playLevel.getName();
                        if (WorldTools.unloadLevel(playLevel, true)) {
                            GameAPI.getGameDebugManager().info("Successfully delete map: " + levelName);
                        } else {
                            GameAPI.getGameDebugManager().error("Fail to delete map: " + levelName);
                        }
                    }
                }
            }
            RoomManager.unloadRoom(this);
        } else {
            if (this.resetMap) {
                if (this.playLevels == null) {
                    GameAPI.getInstance().getLogger().warning("Unable to find the unloading map, room name: " + this.getRoomName());
                    RoomManager.unloadRoom(this);
                    return;
                }
                GameAPI.getInstance().getLogger().alert(GameAPI.getLanguage().getTranslation("room.reset.room_and_map", this.getRoomName()));
                if (WorldTools.unloadAndReloadLevels(this)) {
                    this.roomTaskExecutor = Executors.newScheduledThreadPool(4);
                    this.getRoomTaskExecutor().scheduleAtFixedRate(this.getRoomUpdateTask(), 0, GameAPI.GAME_TASK_INTERVAL * 50, TimeUnit.MILLISECONDS);
                    this.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_WAIT, "internal");
                }
            } else {
                GameAPI.getInstance().getLogger().alert(GameAPI.getLanguage().getTranslation("room.reset.only_room", this.getRoomName()));
                this.roomTaskExecutor = Executors.newScheduledThreadPool(4);
                this.getRoomTaskExecutor().scheduleAtFixedRate(this.getRoomUpdateTask(), 0, GameAPI.GAME_TASK_INTERVAL * 50, TimeUnit.MILLISECONDS);
                this.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_WAIT, "internal");
            }
        }
    }

    public void setWaitSpawn(String position) {
        this.setWaitSpawn(new AdvancedLocation(position));
    }

    public void addStartSpawn(String position) {
        this.addStartSpawn(new AdvancedLocation(position));
    }

    public void addSpectatorSpawn(String position) {
        this.addSpectatorSpawn(new AdvancedLocation(position));
    }

    public void setEndSpawn(String position) {
        this.setEndSpawn(new AdvancedLocation(position));
    }

    public void setWaitSpawn(Location location) {
        this.setWaitSpawn(new AdvancedLocation(location));
    }

    public void addStartSpawn(Location location) {
        this.addStartSpawn(new AdvancedLocation(location));
    }

    public void addSpectatorSpawn(Location location) {
        this.addSpectatorSpawn(new AdvancedLocation(location));
    }

    public void setEndSpawn(Location location) {
        this.setEndSpawn(new AdvancedLocation(location));
    }

    public void setWaitSpawn(AdvancedLocation location) {
        this.waitSpawn = location;
    }

    public void addStartSpawn(AdvancedLocation location) {
        this.startSpawn.add(location);
    }

    public void addSpectatorSpawn(AdvancedLocation location) {
        this.spectatorSpawn.add(location);
    }

    public void setEndSpawn(AdvancedLocation location) {
        this.endSpawn = location;
    }

    public boolean hasPlayer(Player player) {
        return this.players.contains(player);
    }

    public boolean hasSpectator(Player player) {
        return this.spectators.contains(player);
    }

    public void removeSpectator(Player player) {
        if (!this.getSpectators().contains(player)) {
            return;
        }
        Location location;
        if (this.getEndSpawn() != null && this.getEndSpawn().getLocation() != null && this.getEndSpawn().getLocation().isValid()) {
            location = this.getEndSpawn().getLocation();
        } else {
            location = Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation();
        }
        RoomSpectatorLeaveEvent roomSpectatorLeaveEvent = new RoomSpectatorLeaveEvent(this, player, location);
        GameListenerRegistry.callEvent(this, roomSpectatorLeaveEvent);
        if (roomSpectatorLeaveEvent.isCancelled()) {
            return;
        }
        if (GameAPI.getInstance().isTipsEnabled()) {
            for (Level playLevel : this.getPlayLevels()) {
                TipsTools.removeTipsConfig(playLevel.getName(), player, this.getRoomRule().getTipHideElements().toArray(new TipElementType[0]));
            }
        }
        ScoreboardManager.removeScoreboard(player);
        player.setNameTagVisible(true);
        player.setNameTagAlwaysVisible(true);
        player.removeAllEffects();
        player.setGamemode(Server.getInstance().getDefaultGamemode());
        this.resetSpeed(player);
        player.sendMessage(GameAPI.getLanguage().getTranslation("room.spectator.quit"));
        this.spectators.remove(player);
        RoomManager.getPlayerRoomHashMap().remove(player);
        this.removeHideStatus(player);
        player.teleport(roomSpectatorLeaveEvent.getReturnLocation());
    }

    public void processSpectatorJoin(Player player) {
        if (RoomManager.getRoom(player) != null) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_other_room"));
            return;
        }
        if (!this.getCurrentRoomStatus().isAllowSpectatorJoin(this)) {
            // Player are not allowed to become spectators after the game wrap up(Aka: after RoomGameEnd).
            GameAPI.getLanguage().getTranslation("room.spectator.join.not_allowed");
            return;
        }
        Location defaultTeleportLocation = null;
        switch (this.getCurrentRoomStatus().getIdentifier()) {
            case RoomDefaultStatusFactory.ROOM_STATUS_READY_START_ID:
            case RoomDefaultStatusFactory.ROOM_STATUS_GAME_START_ID:
                if (!this.getSpectatorSpawn().isEmpty()) {
                    int randomInt = ThreadLocalRandom.current().nextInt(this.getSpectatorSpawn().size());
                    defaultTeleportLocation = this.getSpectatorSpawn().get(randomInt).getLocation();
                } else {
                    if (!this.getStartSpawn().isEmpty()) {
                        Random random = new Random(this.getStartSpawn().size());
                        AdvancedLocation location = this.getStartSpawn().get(random.nextInt(this.getStartSpawn().size()));
                        defaultTeleportLocation = location.getLocation();
                    } else {
                        if (!this.players.isEmpty()) {
                            defaultTeleportLocation = this.players.get(0).getLocation();
                        }
                    }
                }
                break;
            case RoomDefaultStatusFactory.ROOM_STATUS_WAIT_ID:
            case RoomDefaultStatusFactory.ROOM_STATUS_PRESTART_ID:
                if (this.getWaitSpawn() != null) {
                    defaultTeleportLocation = this.getWaitSpawn().getLocation();
                }
                break;
        }
        RoomManager.getPlayerRoomHashMap().put(player, this);
        RoomSpectatorJoinEvent roomSpectatorJoinEvent = new RoomSpectatorJoinEvent(this, player, defaultTeleportLocation);
        GameListenerRegistry.callEvent(this, roomSpectatorJoinEvent);
        if (roomSpectatorJoinEvent.isCancelled()) {
            RoomManager.getPlayerRoomHashMap().remove(player);
            return;
        }
        this.spectators.add(player);
        player.removeAllEffects();
        player.setNameTagVisible(false);
        player.setNameTagAlwaysVisible(false);
        if (roomSpectatorJoinEvent.teleportLocation != null) {
            player.teleport(roomSpectatorJoinEvent.getTeleportLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        if (GameAPI.getInstance().isTipsEnabled()) {
            for (Level playLevel : this.getPlayLevels()) {
                TipsTools.closeTipsShow(playLevel.getName(), player, this.getRoomRule().getTipHideElements().toArray(new TipElementType[0]));
            }
        }
        for (Player p : this.getPlayers()) {
            p.sendMessage(GameAPI.getLanguage().getTranslation(p, "room.game.broadcast.join_spectator", player.getName()));
        }
        for (Player p : this.getSpectators()) {
            p.sendMessage(GameAPI.getLanguage().getTranslation(p, "room.game.broadcast.join_spectator", player.getName()));
        }
        if (this.roomRule.getSpectatorGameMode() == 1) {
            setPlayerCreativeSpectator(player);
        } else {
            player.setGamemode(this.roomRule.getSpectatorGameMode());
        }
        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.spectator.join"));
    }

    public void setDeath(Player player) {
        this.setDeath(player, true, true, GameAPI.getLanguage().getTranslation(player, "room.died.title"), GameAPI.getLanguage().getTranslation(player, "room.died.subtitle"));
    }

    public void setDeath(Player player, boolean teleport, boolean sendTitle, String title, String subtitle) {
        RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(this, player, sendTitle, EntityDamageEvent.DamageCause.VOID);
        ev.setRespawn(this.roomRule.isAllowRespawn());
        ev.setTitle(title);
        ev.setTeleport(teleport);
        ev.setSubtitle(subtitle);
        GameListenerRegistry.callEvent(this, ev);
        if (!ev.isCancelled()) {
            if (!ev.isKeepExp()) {
                player.setExperience(0, 0);
            }
            if (!ev.isKeepInventory()) {
                player.getInventory().clearAll();
            }
            if (ev.isSendTitle()) {
                player.sendTitle(ev.getTitle(), ev.getSubtitle(), 10, 20, 10);
            }
            if (this.getRoomRule().isVirtualHealth()) {
                this.roomVirtualHealthManager.setAlive(false);
                this.roomVirtualHealthManager.resetHealth(player);
            } else {
                player.setHealth(player.getMaxHealth());
            }
            player.getFoodData().reset();
            player.extinguish();
            this.resetSpeed(player);

            if (ev.isTeleport()) {
                this.teleportToSpectatorSpawn(player);
            }

            if (ev.isRespawn()) {
                int respawnTicks = this.getRoomRule().getRespawnCoolDownTick();
                this.addRespawnTask(player, respawnTicks);
            } else {
                player.setGamemode(this.getRoomRule().getSpectatorGameMode());
            }
        }
    }

    public void addRespawnTask(Player player) {
        this.addRespawnTask(player, roomRule.getRespawnCoolDownTick());
    }

    public void addRespawnTask(Player player, int tick) {
        RoomPlayerRespawnEvent ev = new RoomPlayerRespawnEvent(this, player, null);
        if (!ev.isCancelled()) {
            if (tick > 0) {
                player.setGamemode(this.roomRule.getSpectatorGameMode());
                Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.getInstance(), () -> {
                    GameListenerRegistry.callEvent(this, ev);
                    if (!ev.isCancelled() && this.getCurrentRoomStatus().isAllowDefaultRespawnEnabled(this)) {
                        player.extinguish();
                        player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.respawn.title"), GameAPI.getLanguage().getTranslation(player, "room.respawn.subtitle"));
                        player.setGamemode(this.roomRule.getGameMode());
                        if (ev.getRespawnLocation() == null) {
                            this.teleportToSpawn(player);
                        } else {
                            player.teleport(ev.getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        }
                        if (this.getRoomRule().isVirtualHealth()) {
                            this.roomVirtualHealthManager.resetHealth(player);
                            this.roomVirtualHealthManager.setAlive(true);
                        } else {
                            player.setHealth(player.getMaxHealth());
                        }
                    }
                }, tick);
            } else {
                GameListenerRegistry.callEvent(this, ev);
                if (!ev.isCancelled() && this.getCurrentRoomStatus().isAllowDefaultRespawnEnabled(this)) {
                    player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.respawn.title"), GameAPI.getLanguage().getTranslation(player, "room.respawn.subtitle"));
                    player.setGamemode(this.roomRule.getGameMode());
                    player.getEffects().clear();
                    if (ev.getRespawnLocation() == null) {
                        this.teleportToSpawn(player);
                    } else {
                        player.teleport(ev.getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                    if (this.getRoomRule().isVirtualHealth()) {
                        this.roomVirtualHealthManager.resetHealth(player);
                        this.roomVirtualHealthManager.setAlive(true);
                    } else {
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }
        }
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public void teleportToSpawn(Player player) {
        if (this.getTeam(player) != null) {
            if (this.getTeam(player).teleportToSpawn(player)) {
                return;
            }
        }
        if (this.startSpawn.size() > 1) {
            Random random = new Random(System.currentTimeMillis());
            AdvancedLocation location = this.getStartSpawn().get(random.nextInt(this.getStartSpawn().size()));
            location.teleport(player);
        } else if (this.getStartSpawn().size() == 1) {
            AdvancedLocation location = this.getStartSpawn().get(0);
            location.teleport(player);
        }
    }

    public void teleportToSpectatorSpawn(Player player) {
        if (this.spectatorSpawn.isEmpty()) {
            if (this.getPlayersWithoutCreate().isEmpty()) {
                if (!this.getPlayLevels().isEmpty()) {
                    player.teleport(this.getPlayLevels().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            } else {
                player.teleport(this.getPlayersWithoutCreate().get(0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        } else {
            AdvancedLocation location = this.getSpectatorSpawn().get(ThreadLocalRandom.current().nextInt(this.getStartSpawn().size()));
            location.teleport(player);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"temporary\":" + temporary +
                ", \"resetMap\":" + resetMap +
                ", \"roomName\":" + "\"" + roomName + "\"" +
                ", \"roomStatus\":" + "\"" + currentRoomStatus + "\"" +
                ", \"maxPlayer\":" + maxPlayer +
                ", \"minPlayer\":" + minPlayer +
                ", \"waitTime\":" + waitTime +
                ", \"gameWaitTime\":" + gameWaitTime +
                ", \"gameTime\":" + gameTime +
                ", \"ceremonyTime\":" + ceremonyTime +
                ", \"MaxRound\":" + maxRound +
                ", \"round\":" + round +
                ", \"time\":" + time +
                ", \"roomLevelBackup\":" + "\"" + roomLevelBackup + "\"" +
                ", \"gameName\":" + "\"" + gameName + "\"" +
                ", \"winConsoleCommands\":" + winConsoleCommands +
                ", \"loseConsoleCommands\":" + loseConsoleCommands +
                '}';
    }

    public void addPlayLevel(Level loadLevel) {
        playLevels.add(loadLevel);
    }

    public void removePlayLevel(Level loadLevel) {
        playLevels.remove(loadLevel);
    }

    public void removeHideStatus(Player player) {
        this.setInternalPlayerProperty(player, INTERNAL_KEY_VISIBLE_TO_PLAYERS, false);
        for (Player player1 : this.getPlayers()) {
            player1.showPlayer(player);
            player.showPlayer(player1);
        }
    }

    public void setPlayerCreativeSpectator(Player player) {
        player.setGamemode(1);
        CreativeContentPacket pk = new CreativeContentPacket();
        pk.entries = new Item[0];
        player.dataPacket(pk);
        setPlayerHideFromPlayers(player, false);
    }

    public void setPlayerHideFromPlayers(Player player, boolean visible) {
        this.setInternalPlayerProperty(player, INTERNAL_KEY_VISIBLE_TO_PLAYERS, visible);
        if (visible) {
            for (Player player1 : this.getPlayers()) {
                player1.showPlayer(player);
            }
        } else {
            for (Player player1 : this.getPlayers()) {
                player1.hidePlayer(player);
            }
        }
    }

    public void hideAllPlayers(Player player) {
        for (Player other : Server.getInstance().getOnlinePlayers().values()) {
            player.hidePlayer(other);
        }
    }

    public void showAllPlayers(Player player) {
        for (Player other : Server.getInstance().getOnlinePlayers().values()) {
            player.showPlayer(other);
        }
    }

    // Message, Tips, Actionbars & Titles
    public void sendMessageToAll(String string) {
        this.sendMessageToAll(string, true);
    }

    public void sendMessageToAll(String string, boolean includeSpectators) {
        PlayerTools.sendMessage(this.players, string);
        if (includeSpectators) {
            PlayerTools.sendMessage(this.spectators, string);
        }
        //GameAPI.getInstance().getLogger().info(string);
    }

    public void sendMessageToAll(GameTextContainer text) {
        this.sendMessageToAll(text, true);
    }

    public void sendMessageToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.players) {
            if (HideChatCommand.hideMessagePlayers.contains(player)) {
                continue;
            }
            player.sendMessage(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.spectators) {
                if (HideChatCommand.hideMessagePlayers.contains(spectator)) {
                    continue;
                }
                spectator.sendMessage(text.getText(spectator));
            }
        }
    }

    public void sendActionbarToAll(String string) {
        this.sendActionbarToAll(string, true);
    }

    public void sendActionbarToAll(String string, boolean includeSpectators) {
        PlayerTools.sendActionbar(this.players, string);
        if (includeSpectators) {
            PlayerTools.sendActionbar(this.spectators, string);
        }
    }

    public void sendActionbarToAll(GameTextContainer text) {
        this.sendActionbarToAll(text, true);
    }

    public void sendActionbarToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.players) {
            player.sendActionBar(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.spectators) {
                spectator.sendActionBar(text.getText(spectator));
            }
        }
    }

    public void sendTitleToAll(String string) {
        this.sendTitleToAll(string, "", true);
    }

    public void sendTitleToAll(String string, String subtitle) {
        this.sendTitleToAll(string, subtitle, true);
    }

    public void sendTitleToAll(String string, String subtitle, boolean includeSpectators) {
        PlayerTools.sendTitle(this.players, string, subtitle);
        if (includeSpectators) {
            PlayerTools.sendTitle(this.spectators, string, subtitle);
        }
    }

    public void sendTitleToAll(TitleData titleData) {
        this.sendTitleToAll(titleData, true);
    }

    public void sendTitleToAll(TitleData titleData, boolean includeSpectators) {
        PlayerTools.sendTitle(this.players, titleData);
        if (includeSpectators) {
            PlayerTools.sendTitle(this.spectators, titleData);
        }
    }

    public void sendTipToAll(String string) {
        this.sendTipToAll(string, true);
    }

    public void sendTipToAll(String string, boolean includeSpectators) {
        PlayerTools.sendTip(this.players, string);
        if (includeSpectators) {
            PlayerTools.sendTip(this.spectators, string);
        }
    }

    public void sendTipToAll(GameTextContainer text) {
        this.sendTipToAll(text, true);
    }

    public void sendTipToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.players) {
            player.sendTip(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.spectators) {
                spectator.sendTip(text.getText(spectator));
            }
        }
    }

    @Deprecated
    public void resetSpeed(Player player) {
        PlayerTools.resetSpeed(player, 0.1f);
    }

    public boolean setRoomNumber(int number) {
        if (RoomManager.getRoom(number) != null) {
            return false;
        } else {
            this.roomNumber = number;
            return true;
        }
    }

    public void addEntityDamageSource(Entity victim, Entity damager, Item item, float damage, EntityDamageEvent sourceEvent) {
        // 获取或创建damage sources列表
        Map<Entity, List<EntityDamageSource>> lastDamageSources = this.getLastEntityReceiveDamageSource();
        List<EntityDamageSource> entityDamageSources = new ArrayList<>(lastDamageSources.getOrDefault(victim, new ArrayList<>()));

        // 计算总伤害并过滤无效项
        float totalDamage = (float) entityDamageSources.stream()
                .filter(Objects::nonNull)
                .mapToDouble(EntityDamageSource::getFinalDamage)
                .sum();

        // 移除已处理的伤害源
        entityDamageSources.removeIf(Objects::nonNull);
        // 添加新的伤害源
        EntityDamageSource newSource = new EntityDamageSource(
                damager,
                item,
                damage,
                totalDamage + damage,
                System.currentTimeMillis(),
                sourceEvent
        );
        entityDamageSources.add(newSource);
        lastDamageSources.put(victim, entityDamageSources);
    }

    public Optional<EntityDamageSource> getLastEntityDamageByEntitySource(Entity victim) {
        List<EntityDamageSource> entityDamageSources = this.getLastEntityReceiveDamageSource().getOrDefault(victim, new ArrayList<>());
        entityDamageSources.removeIf(Objects::isNull);
        entityDamageSources.removeIf(entityDamageSource -> {
            Entity entity = entityDamageSource.getDamager();
            return entity == null || entity.isClosed();
        });
        List<EntityDamageSource> result = entityDamageSources.stream().filter(entityDamageSource -> !entityDamageSource.getDamager().isPlayer).toList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(result.size() - 1));
    }

    public Optional<EntityDamageSource> getLastEntityDamageByPlayerSource(Entity victim) {
        List<EntityDamageSource> entityDamageSources = this.getLastEntityReceiveDamageSource().getOrDefault(victim, new ArrayList<>());
        entityDamageSources.removeIf(entityDamageSource -> {
            Entity entity = entityDamageSource.getDamager();
            return entity == null || entity.isClosed();
        });
        List<EntityDamageSource> result = entityDamageSources.stream().filter(entityDamageSource -> entityDamageSource.getDamager().isPlayer).toList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(result.size() - 1));
    }
}
