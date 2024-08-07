package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.annotation.Future;
import gameapi.event.player.*;
import gameapi.event.room.*;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.extensions.supplyChest.SupplyChest;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.form.element.ResponsiveElementInput;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.room.AdvancedBlockManager;
import gameapi.manager.room.CheckpointManager;
import gameapi.manager.room.RoomVirtualHealthManager;
import gameapi.room.executor.BaseRoomExecutor;
import gameapi.room.executor.RoomExecutor;
import gameapi.room.items.RoomItemBase;
import gameapi.room.state.GameState;
import gameapi.room.team.BaseTeam;
import gameapi.room.utils.HideType;
import gameapi.room.utils.QuitRoomReason;
import gameapi.tools.PlayerTools;
import gameapi.tools.TipsTools;
import gameapi.tools.WorldTools;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.Language;
import it.unimi.dsi.fastutil.Function;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
@Data
public class Room {

    @Setter(AccessLevel.NONE)
    protected ConcurrentHashMap<String, BaseTeam> teamCache = new ConcurrentHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, LinkedHashMap<String, Object>> playerProperties = new LinkedHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, Object> roomProperties = new LinkedHashMap<>();
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, Object> inheritProperties = new LinkedHashMap<>();
    protected String joinPassword = "";
    // Used as a temporary room and will be deleted after the game.
    private RoomExecutor statusExecutor = new BaseRoomExecutor(this);
    private boolean temporary = false;
    private boolean resetMap = true;
    private String roomName = "";
    private RoomRule roomRule;
    private RoomStatus roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    private List<Player> players = new ArrayList<>();
    private List<Player> spectators = new ArrayList<>();
    private int maxPlayer = 2;
    private int minPlayer = 16;
    private int waitTime = 10;
    private int gameWaitTime = 10;
    private int gameTime = 10;
    private int ceremonyTime = 10;
    private int gameEndTime = 10;
    private int nextRoundPreStartTime = 10;
    private int maxRound;
    private int round = 0;
    private int time = 0; // Spent Seconds
    private boolean isAllowedToStart = true;
    private List<Level> playLevels = new ArrayList<>();
    private AdvancedLocation waitSpawn = new AdvancedLocation();
    private List<AdvancedLocation> startSpawn = new ArrayList<>();
    private AdvancedLocation endSpawn;
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

    @Future
    private List<GameState> gameStates = new ArrayList<>();
    @Future
    private int gameStateCurrentIndex = 0;

    private List<SupplyChest> supplyChests = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private RoomUpdateTask roomUpdateTask;
    @Setter(AccessLevel.NONE)
    private LinkedHashMap<String, RoomItemBase> roomItems = new LinkedHashMap<>();

    private CheckpointManager checkpointManager = new CheckpointManager();

    @Setter(AccessLevel.NONE)
    private List<DynamicObstacle> dynamicObstacles = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private RoomVirtualHealthManager roomVirtualHealthManager = new RoomVirtualHealthManager(this);
    private ScheduledExecutorService roomTaskExecutor = Executors.newSingleThreadScheduledExecutor();
    private AdvancedBlockManager advancedBlockManager = new AdvancedBlockManager();

    public Room(String gameName, RoomRule roomRule, int round) {
        this(gameName, roomRule, "", round);
    }

    public Room(String gameName, RoomRule roomRule, String roomLevelBackup, int round) {
        this.maxRound = round;
        this.roomRule = roomRule;
        this.gameName = gameName;
        this.roomUpdateTask = new RoomUpdateTask(this);
        this.roomLevelBackup = roomLevelBackup;
    }

    public void registerRoomItem(RoomItemBase... roomItems) {
        for (RoomItemBase roomItem : roomItems) {
            this.roomItems.put(roomItem.getIdentifier(), roomItem);
        }
    }

    public RoomItemBase getRoomItem(String identifier) {
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

    public boolean hasPlayerProperty(String player, String key) {
        if (this.playerProperties.containsKey(player)) {
            return this.playerProperties.get(player).containsKey(key);
        } else {
            return false;
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

    public void executeLoseCommands(Player player) {
        for (String string : this.loseConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", "\"" + player.getName() + "\"").replace("%level%", player.getLevel().getName()).replace("%game_name%", gameName).replace("%room_name%", roomName));
        }
    }

    public void executeWinCommands(Player player) {
        for (String string : this.winConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", "\"" + player.getName() + "\"").replace("%level%", player.getLevel().getName()).replace("%game_name%", gameName).replace("%room_name%", roomName));
        }
    }

    public void allocatePlayerToTeams() {
        if (this.teamCache.keySet().size() == 0) {
            return;
        }
        for (Player player : this.players) {
            Map<String, BaseTeam> map = new ConcurrentHashMap<>(this.teamCache);
            List<Map.Entry<String, BaseTeam>> list = map.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(t -> t.getValue().getSize()))
                    .collect(Collectors.toList());
            this.teamCache.get(list.get(0).getKey()).addPlayer(player); //从最低人数来尝试加入
            BaseTeam team = list.get(0).getValue();
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", team.getPrefix() + team.getRegistryName()));
        }
    }

    public boolean addTeamPlayer(String registry, Player player) {
        if (getTeam(player) != null) {
            return false;
        }
        if (this.teamCache.containsKey(registry) && this.teamCache.get(registry).isAvailable()) { //禁止加入满人队伍
            this.teamCache.get(registry).addPlayer(player);
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", this.teamCache.get(registry).getPrefix() + registry));
        } else {
            return false;
        }
        return true;
    }

    public void removePlayerFromTeam(Player player) {
        for (Map.Entry<String, BaseTeam> entrySet : this.teamCache.entrySet()) {
            if (entrySet.getValue().hasPlayer(player)) {
                BaseTeam team = entrySet.getValue();
                this.teamCache.get(entrySet.getKey()).removePlayer(player);
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.quit", team.getPrefix() + team.getRegistryName()));
            }
        }
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

    public List<BaseTeam> getOpponentTeams(BaseTeam baseTeam) {
        List<BaseTeam> baseTeams = new ArrayList<>(this.teamCache.values());
        baseTeams.remove(baseTeam);
        return baseTeams;
    }

    public BaseTeam getTeam(String teamId) {
        return this.teamCache.getOrDefault(teamId, null);
    }

    public void registerTeam(BaseTeam team) {
        this.teamCache.put(team.getRegistryName(), team);
    }

    /*
        Here we genuinely add an authentication process,
        which aims to serve the server hosting some big events
     */
    public void addPlayer(Player player) {
        if (!joinPassword.isEmpty()) {
            String rightPassword = this.getJoinPassword();
            AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(GameAPI.getLanguage().getTranslation(player, "room.window.password.title"))
                    .input(
                            new ResponsiveElementInput(GameAPI.getLanguage().getTranslation(player, "room.window.password.input_text"))
                                    .onRespond((player1, s) -> {
                                        if (rightPassword.equals(s)) {
                                            processPlayerJoin(player1);
                                        } else {
                                            player1.sendMessage(GameAPI.getLanguage().getTranslation(player1, "room.password.wrong"));
                                        }
                                    })
                    );
            custom.showToPlayer(player);
        } else {
            processPlayerJoin(player);
        }
    }

    public void processPlayerJoin(Player player) {
        if (RoomManager.getRoom(player) != null) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_other_room"));
            return;
        }
        if (!this.getJoinPassword().equals(this.joinPassword)) {
            player.sendMessage(GameAPI.getLanguage().getTranslation("command.error.incorrect_password"));
            return;
        }
        List<String> whitelists = this.getRoomRule().getAllowJoinPlayers();
        if (whitelists.size() > 0) {
            if (!whitelists.contains(player.getName())) {
                player.sendMessage(GameAPI.getLanguage().getTranslation("room.game.no_access"));
                return;
            }
        }
        if (this.roomStatus == RoomStatus.ROOM_MAP_LOAD_FAILED) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.load_failed"));
            return;
        }
        if (this.roomStatus == RoomStatus.ROOM_MAP_INITIALIZING) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.resetting"));
            return;
        }
        if (this.roomStatus == RoomStatus.ROOM_HALTED) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.halted"));
            return;
        }

        if (this.roomStatus != RoomStatus.ROOM_STATUS_WAIT && this.roomStatus != RoomStatus.ROOM_STATUS_PRESTART) {
            if (!this.roomRule.isAllowJoinAfterStart()) {
                if (this.getRoomRule().isAllowSpectators()) {
                    this.processJoinSpectator(player);
                } else {
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.started"));
                }
                return;
            }
        }

        if (this.players.size() < this.maxPlayer) {
            if (this.hasPlayer(player) || this.hasSpectator(player)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_this_room"));
            } else {
                RoomPlayerPreJoinEvent ev = new RoomPlayerPreJoinEvent(this, player);
                GameListenerRegistry.callEvent(this, ev);
                if (!ev.isCancelled()) {
                    this.addPlayerToRoom(player);
                    this.roomUpdateTask.setPlayerLastLocation(player, player.getLocation());
                    this.playerProperties.computeIfAbsent(player.getName(), (Function<String, LinkedHashMap<String, Object>>) o -> new LinkedHashMap<>());
                    this.players.add(player);
                    this.waitSpawn.teleport(player);
                    player.setGamemode(2);
                    player.getFoodData().reset();
                    player.setFoodEnabled(this.getRoomRule().isAllowFoodLevelChange());
                    player.setNameTagVisible(true);
                    player.setNameTagAlwaysVisible(true);
                    player.setHealth(player.getMaxHealth());
                    for (Player p : this.players) {
                        p.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.broadcast.join", player.getName(), this.players.size(), this.maxPlayer));
                    }
                    this.hidePlayer(player, this.getRoomRule().getHideType());
                    this.updateHideStatus(player, false);
                    GameListenerRegistry.callEvent(this, new RoomPlayerJoinEvent(this, player));
                }
            }
        }
    }

    public void addPlayerToRoom(Player player) {
        RoomManager.getPlayerRoomHashMap().put(player, this);
        this.playerProperties.computeIfAbsent(player.getName(), (Function<String, LinkedHashMap<String, Object>>) o -> new LinkedHashMap<>());
    }

    public void removePlayer(Player player) {
        this.removePlayer(player, QuitRoomReason.DEFAULT);
    }

    public void removePlayer(Player player, QuitRoomReason reason) {
        RoomPlayerLeaveEvent ev = new RoomPlayerLeaveEvent(this, player);
        GameListenerRegistry.callEvent(this, ev);
        if (!ev.isCancelled()) {
            RoomManager.getPlayerRoomHashMap().remove(player);

            if (!this.getRoomRule().isSavePlayerPropertiesAfterQuit()) {
                this.playerProperties.remove(player.getName());
            }
            for (Player p : this.getPlayers()) {
                p.sendMessage(GameAPI.getLanguage().getTranslation(p, "baseEvent.quit.success", player.getName()));
            }
            if (GameAPI.getInstance().isTipsEnabled()) {
                for (Level playLevel : this.getPlayLevels()) {
                    TipsTools.removeTipsConfig(playLevel.getName(), player);
                }
            }
            player.getFoodData().reset();
            player.setFoodEnabled(true);
            player.removeAllEffects();
            player.setHealth(player.getMaxHealth());
            player.getEffects().clear();
            player.setNameTag("");
            player.setGamemode(Server.getInstance().getDefaultGamemode());
            this.roomVirtualHealthManager.removePlayer(player);
            this.removePlayerFromTeam(player);
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
            this.updateHideStatus(player, true);

            this.players.remove(player);
        }
    }

    public void setRoomStatus(RoomStatus status) {
        this.time = 0;
        switch (status) {
            case ROOM_STATUS_PRESTART:
                GameListenerRegistry.callEvent(this, new RoomPreStartEvent(this));
                break;
            case ROOM_STATUS_READY_START:
                GameListenerRegistry.callEvent(this, new RoomReadyStartEvent(this));
                break;
            case ROOM_STATUS_START:
                GameListenerRegistry.callEvent(this, new RoomGameStartEvent(this));
                break;
            case ROOM_STATUS_GAME_END:
                GameListenerRegistry.callEvent(this, new RoomGameEndEvent(this));
                break;
            case ROOM_STATUS_CEREMONY:
                GameListenerRegistry.callEvent(this, new RoomCeremonyEvent(this));
                break;
            case ROOM_STATUS_NEXT_ROUND_PRESTART:
                GameListenerRegistry.callEvent(this, new RoomNextRoundPreStartEvent(this));
                break;
        }
        this.roomStatus = status;
    }

    public void resetAll() {
        if (this.roomStatus == RoomStatus.ROOM_MAP_INITIALIZING) {
            return;
        }
        this.getRoomTaskExecutor().shutdown();
        this.setRoomStatus(RoomStatus.ROOM_MAP_INITIALIZING);
        GameListenerRegistry.callEvent(this, new RoomResetEvent(this));
        for (Player player : new ArrayList<>(this.spectators)) {
            this.removeSpectator(player);
        }
        for (Player player : new ArrayList<>(this.players)) {
            this.removePlayer(player);
        }
        //因为某些原因无法正常传送走玩家，就全部踹出服务器！
        this.players = new ArrayList<>();
        this.round = 0;
        this.time = 0;
        this.playerProperties = new LinkedHashMap<>();
        this.teamCache.forEach((s, team) -> team.resetAll());
        this.chatDataList = new ArrayList<>();
        this.getCheckpointManager().clearAllPlayerCheckPointData();
        this.roomVirtualHealthManager.clearAll();
        for (SupplyChest supplyChest : this.getSupplyChests()) {
            supplyChest.resetData();
        }
        if (this.playLevels == null) {
            GameAPI.getInstance().getLogger().warning("Unable to find the unloading map, room name: " + this.getRoomName());
            return;
        }
        // 增加默认地图判断
        for (Level playLevel : this.playLevels) {
            if (playLevel != Server.getInstance().getDefaultLevel()) {
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
                    if (playLevel != null) {
                        WorldTools.unloadLevel(playLevel, true);
                    }
                }
            }
            RoomManager.unloadRoom(this);
        } else {
            if (this.resetMap) {
                GameAPI.getInstance().getLogger().alert(GameAPI.getLanguage().getTranslation("room.reset.room_and_map", this.getRoomName()));
                if (WorldTools.unloadAndReloadLevels(this)) {
                    this.roomTaskExecutor = Executors.newSingleThreadScheduledExecutor();
                    this.getRoomTaskExecutor().scheduleAtFixedRate(this.getRoomUpdateTask(), 0, GameAPI.GAME_TASK_INTERVAL * 50, TimeUnit.MILLISECONDS);
                    this.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                }
            } else {
                GameAPI.getInstance().getLogger().alert(GameAPI.getLanguage().getTranslation("room.reset.only_room", this.getRoomName()));
                this.roomTaskExecutor = Executors.newSingleThreadScheduledExecutor();
                this.getRoomTaskExecutor().scheduleAtFixedRate(this.getRoomUpdateTask(), 0, GameAPI.GAME_TASK_INTERVAL * 50, TimeUnit.MILLISECONDS);
                this.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
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
        RoomSpectatorLeaveEvent roomSpectatorLeaveEvent = new RoomSpectatorLeaveEvent(this, player, Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
        GameListenerRegistry.callEvent(this, roomSpectatorLeaveEvent);
        if (roomSpectatorLeaveEvent.isCancelled()) {
            return;
        }
        if (GameAPI.getInstance().isTipsEnabled()) {
            for (Level playLevel : this.getPlayLevels()) {
                TipsTools.removeTipsConfig(playLevel.getName(), player);
            }
        }
        player.setGamemode(Server.getInstance().getDefaultGamemode());
        player.teleport(roomSpectatorLeaveEvent.getReturnLocation());
        player.sendMessage(GameAPI.getLanguage().getTranslation("room.spectator.quit"));
        RoomManager.getPlayerRoomHashMap().remove(player);
        spectators.remove(player);
    }

    public void processJoinSpectator(Player player) {
        if (RoomManager.getRoom(player) != null) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.already_in_other_room"));
            return;
        }
        if (this.getRoomStatus().ordinal() > 4) {
            // Player are not allowed to become spectators after the game wrap up(Aka: after RoomGameEnd).
            GameAPI.getLanguage().getTranslation("room.spectator.join.not_allowed");
            return;
        }
        RoomSpectatorJoinEvent roomSpectatorJoinEvent = new RoomSpectatorJoinEvent(this, player);
        GameListenerRegistry.callEvent(this, roomSpectatorJoinEvent);
        if (roomSpectatorJoinEvent.isCancelled()) {
            return;
        }
        player.setGamemode(3);
        switch (this.getRoomStatus()) {
            case ROOM_STATUS_READY_START:
            case ROOM_STATUS_START:
                for (Level playLevel : this.getPlayLevels()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
                if (this.getSpectatorSpawn().size() != 0) {
                    Random random = new Random(this.getSpectatorSpawn().size());
                    AdvancedLocation location = this.getSpectatorSpawn().get(random.nextInt(this.getSpectatorSpawn().size()));
                    location.teleport(player);
                } else {
                    if (this.getStartSpawn().size() != 0) {
                        Random random = new Random(this.getStartSpawn().size());
                        AdvancedLocation location = this.getStartSpawn().get(random.nextInt(this.getStartSpawn().size()));
                        location.teleport(player);
                    } else {
                        player.teleport(this.players.get(0).getLocation(), null);
                    }
                }
                break;
            case ROOM_STATUS_WAIT:
            case ROOM_STATUS_PRESTART:
                if (this.getWaitSpawn() != null) {
                    this.getWaitSpawn().teleport(player);
                }
                break;
        }
        this.spectators.add(player);
        RoomManager.getPlayerRoomHashMap().put(player, this);
        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.spectator.join"));
    }

    public void setDeath(Player player) {
        RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(this, player, EntityDamageEvent.DamageCause.VOID);
        GameListenerRegistry.callEvent(this, ev);
        if (!ev.isCancelled()) {
            if (!ev.isKeepExp()) {
                player.setExperience(0, 0);
            }
            if (!ev.isKeepInventory()) {
                player.getInventory().clearAll();
            }
            if (this.roomRule.isVirtualHealth()) {
                this.roomVirtualHealthManager.setHealth(player, this.roomVirtualHealthManager.getMaxHealth());
            }
            player.extinguish();
            player.removeAllEffects();
            player.setGamemode(3);
            player.setHealth(player.getMaxHealth());
            player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.died.title"), GameAPI.getLanguage().getTranslation(player, "room.died.subtitle"), 5, 10, 5);
        }
    }

    public void addRespawnTask(Player player) {
        addRespawnTask(player, roomRule.getRespawnCoolDownTick());
    }

    public void addRespawnTask(Player player, int tick) {
        if (this.spectatorSpawn.size() != 0) {
            Random random = new Random();
            this.spectatorSpawn.get(random.nextInt(spectatorSpawn.size())).teleport(player);
            this.teleportToSpawn(player);
        }
        RoomPlayerRespawnEvent ev = new RoomPlayerRespawnEvent(this, player, null);
        if (!ev.isCancelled()) {
            if (tick > 0) {
                Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.getInstance(), () -> {
                    GameListenerRegistry.callEvent(this, ev);
                    if (!ev.isCancelled() && this.getRoomStatus() == RoomStatus.ROOM_STATUS_START) {
                        player.extinguish();
                        player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.respawn.title"), GameAPI.getLanguage().getTranslation(player, "room.respawn.subtitle"));
                        player.setGamemode(this.roomRule.getGameMode());
                        if (ev.getRespawnLocation() == null) {
                            this.teleportToSpawn(player);
                        } else {
                            player.teleport(ev.getRespawnLocation(), null);
                        }
                        if (this.getRoomRule().isVirtualHealth()) {
                            this.roomVirtualHealthManager.resetHealth(player);
                        } else {
                            player.setHealth(player.getMaxHealth());
                        }
                    }
                }, tick);
            } else {
                GameListenerRegistry.callEvent(this, ev);
                if (!ev.isCancelled() && this.getRoomStatus() == RoomStatus.ROOM_STATUS_START) {
                    player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.respawn.title"), GameAPI.getLanguage().getTranslation(player, "room.respawn.subtitle"));
                    player.setGamemode(roomRule.getGameMode());
                    player.getEffects().clear();
                    Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.getInstance(), () -> player.fireProof = false, 5);
                    if (ev.getRespawnLocation() == null) {
                        teleportToSpawn(player);
                    } else {
                        player.teleport(ev.getRespawnLocation(), null);
                    }
                    if (this.getRoomRule().isVirtualHealth()) {
                        this.roomVirtualHealthManager.resetHealth(player);
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

    public void teleportToSpawn(Player p) {
        if (this.getTeam(p) != null) {
            this.getTeam(p).teleportToSpawn();
            return;
        }
        if (this.startSpawn.size() > 1) {
            if (this.getPlayerProperty(p.getName(), "spawnIndex") == null) {
                Random random = new Random(System.currentTimeMillis());
                AdvancedLocation location = this.getStartSpawn().get(random.nextInt(this.getStartSpawn().size()));
                location.teleport(p);
            } else {
                AdvancedLocation location = this.getStartSpawn().get((Integer) this.getPlayerProperty(p.getName(), "spawnIndex"));
                location.teleport(p);
            }
        } else if (this.getStartSpawn().size() == 1) {
            AdvancedLocation location = this.getStartSpawn().get(0);
            location.teleport(p);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"temporary\":" + temporary +
                ", \"resetMap\":" + resetMap +
                ", \"roomName\":" + "\"" + roomName + "\"" +
                ", \"roomStatus\":" + "\"" + roomStatus + "\"" +
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

    public LinkedHashMap<String, Object> getInheritProperties() {
        return inheritProperties;
    }

    public void setPersonal(Boolean personal, Player player) {
        this.roomRule.setPersonal(personal);
        this.inheritProperties.put("personal_owner", player);
        this.isAllowedToStart = true;
    }

    public void addPlayLevel(Level loadLevel) {
        playLevels.add(loadLevel);
        loadLevel.getGameRules().setGameRule(GameRule.SHOW_TAGS, true);
    }

    public void removePlayLevel(Level loadLevel) {
        playLevels.remove(loadLevel);
    }

    public void updateHideStatus(Player player, boolean isQuit) {
        if (isQuit) {
            for (Player onlinePlayer : Server.getInstance().getOnlinePlayers().values()) {
                if (!player.canSee(onlinePlayer)) {
                    player.showPlayer(onlinePlayer);
                }
            }
        } else if (this.roomRule.getHideType() == HideType.NOT_IN_THE_SAME_ROOM) {
            // 玩家加入房间，如果只有房内可见，只需要更新房内玩家的hidePlayers即可
            for (Player roomPlayer : this.getPlayers()) {
                roomPlayer.showPlayer(player);
            }
        }
    }

    public void hidePlayer(Player player, HideType type) {
        switch (type) {
            case ALL:
                for (Player target : Server.getInstance().getOnlinePlayers().values()) {
                    if (player.canSee(target)) {
                        player.hidePlayer(target);
                    }
                }
                break;
            case NOT_IN_THE_SAME_ROOM:
                Room room = RoomManager.getRoom(player);
                for (Player target : Server.getInstance().getOnlinePlayers().values()) {
                    Room targetRoom = RoomManager.getRoom(target);
                    if (room != targetRoom) {
                        if (player.canSee(target)) {
                            player.hidePlayer(target);
                        }
                    }
                }
                break;
        }
    }

    public void showPlayer(Player player) {
        for (Player value : Server.getInstance().getOnlinePlayers().values()) {
            Room room = RoomManager.getRoom(player);
            Room targetRoom = RoomManager.getRoom(value);
            if (room == targetRoom && !player.canSee(value)) {
                player.showPlayer(value);
            }
        }
    }

    // Message, Tips, Actionbars & Titles
    public void sendMessageToAll(String string) {
        this.sendMessageToAll(string, true);
    }

    public void sendMessageToAll(String string, boolean includeSpectators) {
        PlayerTools.sendMessage(players, string);
        if (includeSpectators) {
            PlayerTools.sendMessage(spectators, string);
        }
    }

    public void sendActionbarToAll(String string) {
        this.sendActionbarToAll(string, true);
    }

    public void sendActionbarToAll(String string, boolean includeSpectators) {
        PlayerTools.sendActionbar(players, string);
        if (includeSpectators) {
            PlayerTools.sendActionbar(spectators, string);
        }
    }

    public void sendTitleToAll(String string) {
        this.sendTitleToAll(string, "", true);
    }

    public void sendTitleToAll(String string, String subtitle) {
        this.sendTitleToAll(string, subtitle, true);
    }

    public void sendTitleToAll(String string, String subtitle, boolean includeSpectators) {
        PlayerTools.sendTitle(players, string, subtitle);
        if (includeSpectators) {
            PlayerTools.sendTitle(spectators, string, subtitle);
        }
    }

    public void sendTipToAll(String string) {
        this.sendTipToAll(string, true);
    }

    public void sendTipToAll(String string, boolean includeSpectators) {
        PlayerTools.sendTip(players, string);
        if (includeSpectators) {
            PlayerTools.sendTip(spectators, string);
        }
    }

    public void sendMessageToAll(Language language, String string, Object... params) {
        this.sendMessageToAll(language, string, true, params);
    }

    public void sendMessageToAll(Language language, String string, boolean includeSpectators, Object... params) {
        PlayerTools.sendMessage(players, language, string, params);
        if (includeSpectators) {
            PlayerTools.sendMessage(spectators, language, string, params);
        }
    }

    public void sendActionbarToAll(Language language, String string, Object... params) {
        this.sendActionbarToAll(language, string, true, params);
    }

    public void sendActionbarToAll(Language language, String string, boolean includeSpectators, Object... params) {
        PlayerTools.sendActionbar(players, language, string, params);
        if (includeSpectators) {
            PlayerTools.sendActionbar(spectators, language, string, params);
        }
    }

    public void sendTitleToAll(Language language, String string, Object... params) {
        this.sendTitleToAll(language, string, true, params);
    }

    public void sendTitleToAll(Language language, String string, boolean includeSpectators, Object... params) {
        PlayerTools.sendTitle(players, language, string, params);
        if (includeSpectators) {
            PlayerTools.sendTitle(spectators, language, string, params);
        }
    }

    public void sendTipToAll(Language language, String string, Object... params) {
        this.sendTipToAll(language, string, true, params);
    }

    public void sendTipToAll(Language language, String string, boolean includeSpectators, Object... params) {
        PlayerTools.sendTip(players, language, string, params);
        if (includeSpectators) {
            PlayerTools.sendTip(spectators, language, string, params);
        }
    }
}
