package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.arena.WorldTools;
import gameapi.event.player.*;
import gameapi.event.room.*;
import gameapi.extensions.checkPoint.Checkpoints;
import gameapi.form.AdvancedFormMain;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.inventory.InventoryTools;
import gameapi.language.Language;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.executor.BaseRoomExecutor;
import gameapi.room.executor.RoomExecutor;
import gameapi.room.items.RoomItemBase;
import gameapi.room.team.BaseTeam;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.PlayerTools;
import gameapi.utils.Tips;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    // Save data for the room' extra configuration.
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, Object> roomProperties = new LinkedHashMap<>();
    // Save data for some inherited properties, used by the author to restore some inner info.
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected LinkedHashMap<String, Object> inheritProperties = new LinkedHashMap<>();
    // Provide this variable in order to realise some functions
    protected String joinPassword = "";
    // Used as a temporary room and will be deleted after the game.
    private RoomExecutor statusExecutor = new BaseRoomExecutor(this);
    private boolean temporary = false;
    private boolean resetMap = true; // Resetting map is default set to false.
    private String roomName = "";
    private RoomRule roomRule;
    private RoomStatus roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    private List<Player> players = new ArrayList<>();
    private int maxPlayer = 2;
    private int minPlayer = 16;
    private int waitTime = 10;
    private int gameWaitTime = 10;
    private int gameTime = 10;
    private int ceremonyTime = 10;
    private int gameEndTime = 10;
    private int maxRound;
    private int round = 0;
    private int time = 0; // Spent Seconds
    private boolean preStartPass = true;
    private List<Player> spectators = new ArrayList<>();
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
    private Checkpoints checkpoints = new Checkpoints();
    private long startMillis;
    @Setter(AccessLevel.NONE)
    private RoomUpdateTask roomUpdateTask;
    @Setter(AccessLevel.NONE)
    private LinkedHashMap<String, RoomItemBase> roomItems = new LinkedHashMap<>();

    @Setter(AccessLevel.NONE)
    private RoomHealthManager roomHealthManager = new RoomHealthManager(this);

    public Room(String gameName, RoomRule roomRule, List<Level> playLevels, String roomLevelBackup, int round) {
        this.maxRound = round;
        this.roomRule = roomRule;
        for (Level playLevel : playLevels) {
            if (playLevel == null) {
                GameAPI.plugin.getLogger().warning("playLevel cannot be null!");
                return;
            }
            this.addPlayLevel(playLevel);
        }
        this.roomLevelBackup = roomLevelBackup;
        this.gameName = gameName;
        this.roomUpdateTask = new RoomUpdateTask(this);
    }

    public Room(String gameName, RoomRule roomRule, Level playLevel, String roomLevelBackup, int round) {
        this(gameName, roomRule, new ArrayList<>(Collections.singletonList(playLevel)), roomLevelBackup, round);
    }

    public void registerRoomItem(RoomItemBase... roomItems) {
        for (RoomItemBase roomItem : roomItems) {
            this.roomItems.put(roomItem.getIdentifier(), roomItem);
        }
    }

    public RoomItemBase getRoomItem(String identifier) {
        return this.roomItems.get(identifier);
    }

    public static boolean isRoomCurrentPlayLevel(Level level) {
        if (GameAPI.playerRoomHashMap.size() > 0) {
            return GameAPI.playerRoomHashMap.values().stream().anyMatch(room -> room != null && room.playLevels.stream().anyMatch(l -> l.equals(level)));
        } else {
            return false;
        }
    }

    public static Optional<Room> getRoom(Level level) {
        if (GameAPI.playerRoomHashMap.size() > 0) {
            return GameAPI.playerRoomHashMap.values().stream().filter(room -> room != null && room.playLevels.stream().anyMatch(l -> l.equals(level))).findFirst();
        } else {
            return Optional.empty();
        }
    }

    public static Room getRoom(String gameName, Player p) {
        if (getRoom(p) == null) {
            return null;
        }
        return getRoom(p).getGameName().equals(gameName) ? getRoom(p) : null;
    }

    public static Room getRoom(Player p) {
        return GameAPI.playerRoomHashMap.getOrDefault(p, null);
    }

    public static Room getRoom(String gameName, String roomName) {
        for (Room room : GameAPI.loadedRooms.getOrDefault(gameName, new ArrayList<>())) {
            if (room.roomName.equals(roomName) && room.gameName.equals(gameName)) {
                return room;
            }
        }
        return null;
    }

    public Object getPlayerProperties(Player player, String key) {
        return this.getPlayerProperties(player.getName(), key);
    }

    public Object getPlayerProperties(Player player, String key, Object defaultValue) {
        return this.getPlayerProperties(player.getName(), key, defaultValue);
    }


    public void setPlayerProperties(Player player, String key, Object value) {
        this.setPlayerProperties(player.getName(), key, value);
    }

    public Object getPlayerProperties(String player, String key) {
        return playerProperties.containsKey(player) ? playerProperties.get(player).getOrDefault(key, null) : null;
    }

    public Object getPlayerProperties(String player, String key, Object defaultValue) {
        return playerProperties.containsKey(player) ? playerProperties.get(player).getOrDefault(key, defaultValue) : defaultValue;
    }

    public void setPlayerProperties(String player, String key, Object value) {
        if (playerProperties.containsKey(player)) {
            playerProperties.get(player).put(key, value);
        } else {
            playerProperties.put(player, new LinkedHashMap<>());
            playerProperties.get(player).put(key, value);
        }
    }

    public boolean hasPlayerProperties(String player, String key) {
        if (playerProperties.containsKey(player)) {
            return playerProperties.get(player).containsKey(key);
        } else {
            return false;
        }
    }

    public Object getRoomProperties(String key) {
        return roomProperties.getOrDefault(key, null);
    }

    public Object getRoomProperties(String key, Object defaultValue) {
        return roomProperties.getOrDefault(key, defaultValue);
    }

    public void setRoomProperties(String key, Object value) {
        this.roomProperties.put(key, value);
    }

    public boolean hasRoomProperties(String key) {
        return roomProperties.containsKey(key);
    }

    public void executeLoseCommands(Player player) {
        for (String string : loseConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()).replace("%game_name%", gameName).replace("%room_name%", roomName));
        }
    }

    public void executeWinCommands(Player player) {
        for (String string : winConsoleCommands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()).replace("%game_name%", gameName).replace("%room_name%", roomName));
        }
    }

    public void allocatePlayerToTeams() {
        if (teamCache.keySet().size() == 0) {
            return;
        }
        for (Player player : players) {
            ConcurrentHashMap<String, BaseTeam> map = new ConcurrentHashMap<>(teamCache);
            List<Map.Entry<String, BaseTeam>> list = map.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(t -> t.getValue().getSize()))
                    .collect(Collectors.toList());
            teamCache.get(list.get(0).getKey()).addPlayer(player); //从最低人数来尝试加入
            BaseTeam team = list.get(0).getValue();
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", team.getPrefix() + team.getRegistryName()));
        }
    }

    public boolean addTeamPlayer(String registry, Player player) {
        if (getPlayerTeam(player) != null) {
            return false;
        }
        if (teamCache.containsKey(registry) && teamCache.get(registry).isAvailable()) { //禁止加入满人队伍
            teamCache.get(registry).addPlayer(player);
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.join", teamCache.get(registry).getPrefix() + registry));
        } else {
            return false;
        }
        return true;
    }

    public void removeTeamPlayer(String registry, Player player) {
        if (teamCache.containsKey(registry)) {
            teamCache.get(registry).removePlayer(player);
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.team.quit", teamCache.get(registry).getPrefix() + registry));
        }
    }

    public void removeTeamPlayer(Player player) {
        for (Map.Entry<String, BaseTeam> entrySet : teamCache.entrySet()) {
            if (entrySet.getValue().hasPlayer(player)) {
                teamCache.get(entrySet.getKey()).removePlayer(player);
            }
        }
    }

    public BaseTeam getPlayerTeam(Player player) {
        for (Map.Entry<String, BaseTeam> entrySet : teamCache.entrySet()) {
            if (entrySet.getValue().hasPlayer(player)) {
                return entrySet.getValue();
            }
        }
        return null;
    }

    public List<BaseTeam> getTeams() {
        return new ArrayList<>(teamCache.values());
    }

    public List<BaseTeam> getOpponentTeams(BaseTeam baseTeam) {
        List<BaseTeam> baseTeams = new ArrayList<>(teamCache.values());
        baseTeams.remove(baseTeam);
        return baseTeams;
    }

    public BaseTeam getTeam(String registry) {
        return teamCache.getOrDefault(registry, null);
    }

    public void registerTeam(BaseTeam team) {
        teamCache.put(team.getRegistryName(), team);
    }

    /*
        Here we genuinely add an authentication process,
        which aims to serve the server hosting some big events
     */
    public void addPlayer(Player player) {
        if (!joinPassword.isEmpty()) {
            AdvancedFormWindowCustom.Builder builder = new AdvancedFormWindowCustom.Builder();
            builder.setTitle(GameAPI.getLanguage().getTranslation(player, "room.window.password.title"));
            builder.addElement(new ElementInput(GameAPI.getLanguage().getTranslation(player, "room.window.password.inputText")));
            String rightPassword = this.getJoinPassword();
            builder.onResponse((dealPlayer, responseCustom) -> {
                if (rightPassword.equals(responseCustom.getInputResponse(0))) {
                    processPlayerJoin(dealPlayer);
                } else {
                    player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.password.wrong"));
                }
            });
            AdvancedFormMain.showFormWindow(player, builder.build());
        } else {
            processPlayerJoin(player);
        }
    }

    public void processPlayerJoin(Player player) {
        if (Room.getRoom(player) != null) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.isInOtherRoom"));
            return;
        }
        if (!this.getJoinPassword().equals(joinPassword)) {
            player.sendMessage(GameAPI.getLanguage().getTranslation("command.error.incorrectPassword"));
            return;
        }
        List<String> whitelists = this.getRoomRule().getAllowJoinPlayers();
        if (whitelists.size() > 0) {
            if (!whitelists.contains(player.getName())) {
                player.sendMessage(GameAPI.getLanguage().getTranslation("room.game.noAccess"));
                return;
            }
        }
        RoomStatus roomStatus = this.getRoomStatus();
        if (roomStatus == RoomStatus.ROOM_MapLoadFailed || roomStatus == RoomStatus.ROOM_MapProcessFailed) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.loadFailed"));
            return;
        }
        if (roomStatus == RoomStatus.ROOM_MapInitializing) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.resetting"));
            return;
        }
        if (roomStatus == RoomStatus.ROOM_HALTED) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.map.halted"));
            return;
        }
        if (roomStatus != RoomStatus.ROOM_STATUS_WAIT && roomStatus != RoomStatus.ROOM_STATUS_PreStart) {
            if (this.getRoomRule().isAllowSpectators()) {
                this.processJoinSpectator(player);
            } else {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.started"));
            }
            return;
        }
        if (this.players.size() < this.maxPlayer) {
            if (this.hasPlayer(player)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.isInThisRoom"));
            } else {
                RoomPlayerPreJoinEvent ev = new RoomPlayerPreJoinEvent(this, player);
                GameListenerRegistry.callEvent(this, ev);
                if (!ev.isCancelled()) {
                    roomUpdateTask.setPlayerLastLocation(player, player.getLocation());
                    GameAPI.playerRoomHashMap.put(player, this);
                    InventoryTools.saveBag(player);
                    playerProperties.put(player.getName(), new LinkedHashMap<>());
                    this.players.add(player);
                    waitSpawn.teleport(player);
                    player.setGamemode(2);
                    player.getFoodData().reset();
                    player.setHealth(player.getMaxHealth());
                    for (Player p : this.players) {
                        p.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.broadcast.join", player.getName(), this.players.size(), this.maxPlayer));
                    }
                    GameListenerRegistry.callEvent(this, new RoomPlayerJoinEvent(this, player));
                }
            }
        }
    }

    public void removePlayer(Player player) {
        removePlayer(player, GameAPI.saveBag);
    }

    public void removePlayer(Player player, Boolean saveBag) {
        RoomPlayerLeaveEvent ev = new RoomPlayerLeaveEvent(this, player);
        GameListenerRegistry.callEvent(this, ev);
        if (!ev.isCancelled()) {
            for (Level playLevel : this.getPlayLevels()) {
                Tips.removeTipsConfig(playLevel.getName(), player);
            }
            player.removeAllEffects();
            player.getInventory().clearAll();
            player.setExperience(0, 0);
            player.setHealth(player.getMaxHealth());
            if (saveBag) {
                InventoryTools.loadBag(player);
            }
            player.setGamemode(Server.getInstance().getDefaultGamemode());
            if (this.getPlayerTeam(player) != null) {
                this.getPlayerTeam(player).removePlayer(player);
            }
            this.playerProperties.remove(player.getName());
            this.players.remove(player);
            this.roomHealthManager.removePlayer(player);
            GameAPI.playerRoomHashMap.remove(player);
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
        }
    }

    public void setRoomStatus(RoomStatus status) {
        this.time = 0;
        switch (status) {
            case ROOM_STATUS_GameReadyStart:
                GameListenerRegistry.callEvent(this, new RoomReadyStartEvent(this));
                break;
            case ROOM_STATUS_PreStart:
                GameListenerRegistry.callEvent(this, new RoomPreStartEvent(this));
                break;
            case ROOM_STATUS_GameStart:
                GameListenerRegistry.callEvent(this, new RoomGameStartEvent(this));
                break;
            case ROOM_STATUS_GameEnd:
                GameListenerRegistry.callEvent(this, new RoomGameEndEvent(this));
                break;
            case ROOM_STATUS_Ceremony:
                GameListenerRegistry.callEvent(this, new RoomCeremonyEvent(this));
                break;
        }
        this.roomStatus = status;
    }

    public void resetAll() {
        if (this.roomStatus == RoomStatus.ROOM_MapInitializing) {
            return;
        }
        this.setRoomStatus(RoomStatus.ROOM_MapInitializing);
        for (Player player : new ArrayList<>(spectators)) {
            this.removeSpectator(player);
        }
        for (Player player : new ArrayList<>(players)) {
            this.removePlayer(player);
        }
        //因为某些原因无法正常传送走玩家，就全部踹出服务器！
        this.players = new ArrayList<>();
        this.round = 0;
        this.time = 0;
        this.playerProperties = new LinkedHashMap<>();
        this.teamCache.forEach((s, team) -> team.resetAll());
        this.chatDataList = new ArrayList<>();
        this.getCheckpoints().clearAllPlayerCheckPointData();
        this.roomHealthManager.clearAll();
        if (this.playLevels == null) {
            GameAPI.plugin.getLogger().warning("Unable to find the unloading map, room name: " + this.getRoomName());
            return;
        }
        for (Level playLevel : this.playLevels) {
            for (Player player : playLevel.getPlayers().values()) {
                player.kick("Teleport error!");
            }
        }
        if (this.temporary) {
            GameAPI.plugin.getLogger().alert(GameAPI.getLanguage().getTranslation("room.detect_delete", this.getRoomName()));
            for (Level playLevel : this.playLevels) {
                if (playLevel != null) {
                    WorldTools.unloadLevel(playLevel, true);
                }
            }
            GameAPI.unloadRoom(this);
        } else {
            if (this.resetMap) {
                GameAPI.plugin.getLogger().alert(GameAPI.getLanguage().getTranslation("room.detect_resetRoomAndMap", this.getRoomName()));
                if (WorldTools.unloadAndReloadLevels(this)) {
                    this.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                }
            } else {
                GameAPI.plugin.getLogger().alert(GameAPI.getLanguage().getTranslation("room.detect_resetRoom", this.getRoomName()));
                this.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
            }
        }
    }

    public AdvancedLocation getLocationByString(String string) {
        String[] positions = string.split(":");
        if (positions.length < 4) {
            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation("advancedLocation.error.formatWrong"));
            return null;
        }
        if (!Server.getInstance().isLevelLoaded(positions[3])) {
            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation("advancedLocation.error.worldTryingToLoad"));
            if (Server.getInstance().loadLevel(positions[3])) {
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if (positions.length >= 6) {
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if (positions.length == 7) {
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
                    }
                }
                return advancedLocation;
            } else {
                GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation("advancedLocation.error.worldLoadedFailed", positions[3]));
                return null;
            }
        } else {
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(0);
            if (positions.length >= 6) {
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if (positions.length == 7) {
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }

    public void setWaitSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if (location != null) {
            this.waitSpawn = location;
        } else {
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed);
        }
    }

    public void addStartSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if (location != null) {
            this.startSpawn.add(location);
        } else {
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed);
        }
    }

    public void addSpectatorSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if (location != null) {
            this.spectatorSpawn.add(location);
        } else {
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed);
        }
    }

    public void setEndSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if (location != null) {
            this.endSpawn = location;
        } else {
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed);
        }
    }

    public void setWaitSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.waitSpawn = advancedLocation;
    }

    public void addStartSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.startSpawn.add(advancedLocation);
    }

    public void addSpectatorSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.spectatorSpawn.add(advancedLocation);
    }

    public void setEndSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.endSpawn = advancedLocation;
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
        return players.contains(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void removeSpectator(Player player) {
        RoomSpectatorLeaveEvent roomSpectatorLeaveEvent = new RoomSpectatorLeaveEvent(this, player, Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
        GameListenerRegistry.callEvent(this, roomSpectatorLeaveEvent);
        if (roomSpectatorLeaveEvent.isCancelled()) {
            return;
        }
        for (Level playLevel : this.getPlayLevels()) {
            Tips.removeTipsConfig(playLevel.getName(), player);
        }
        player.setGamemode(Server.getInstance().getDefaultGamemode());
        player.teleport(roomSpectatorLeaveEvent.getReturnLocation());
        player.sendMessage(GameAPI.getLanguage().getTranslation("room.spectator.quit"));
        GameAPI.playerRoomHashMap.remove(player);
        spectators.remove(player);
    }

    public void processJoinSpectator(Player player) {
        if (Room.getRoom(player) != null) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.game.isInOtherRoom"));
            return;
        }
        if (this.getRoomStatus().ordinal() > 4) {
            // Player are not allowed to become spectators after the game wrap up(Aka: after RoomGameEnd).
            GameAPI.getLanguage().getTranslation("room.spectator.join.notAllowed");
            return;
        }
        RoomSpectatorJoinEvent roomSpectatorJoinEvent = new RoomSpectatorJoinEvent(this, player);
        GameListenerRegistry.callEvent(this, roomSpectatorJoinEvent);
        if (roomSpectatorJoinEvent.isCancelled()) {
            return;
        }
        player.setGamemode(3);
        switch (this.getRoomStatus()) {
            case ROOM_STATUS_GameReadyStart:
            case ROOM_STATUS_GameStart:
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
                        player.teleport(players.get(0).getLocation(), null);
                    }
                }
                break;
            case ROOM_STATUS_WAIT:
            case ROOM_STATUS_PreStart:
                if (this.getWaitSpawn() != null) {
                    this.getWaitSpawn().teleport(player);
                }
                break;
        }
        spectators.add(player);
        GameAPI.playerRoomHashMap.put(player, this);
        player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.spectator.join"));
    }

    public void processRespawn(Player player, int originalGamemode) {
        player.getInventory().clearAll();
        player.setGamemode(originalGamemode);
        player.setHealth(player.getMaxHealth());
        player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.died.title"), GameAPI.getLanguage().getTranslation(player, "room.died.subtitle"), 5, 10, 5);
        if (spectatorSpawn.size() != 0) {
            Random random = new Random();
            spectatorSpawn.get(random.nextInt(spectatorSpawn.size())).teleport(player);
            teleportToSpawn(player);
        }
        RoomPlayerRespawnEvent ev = new RoomPlayerRespawnEvent(this, player);
        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, () -> {
            GameListenerRegistry.callEvent(this, ev);
            if (!ev.isCancelled() && this.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                player.sendTitle(GameAPI.getLanguage().getTranslation(player, "room.respawn.title"), GameAPI.getLanguage().getTranslation(player, "room.respawn.subtitle"));
                player.setGamemode(0);
                teleportToSpawn(player);
            }
        }, roomRule.getRespawnCoolDownTick());
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public void teleportToSpawn(Player p) {
        if (this.getPlayerTeam(p) != null) {
            this.getPlayerTeam(p).teleportToSpawn();
            return;
        }
        if (this.startSpawn.size() > 1) {
            if (this.getPlayerProperties(p.getName(), "spawnIndex") == null) {
                Random random = new Random(System.currentTimeMillis());
                AdvancedLocation location = this.startSpawn.get(random.nextInt(this.startSpawn.size()));
                location.teleport(p);
            } else {
                AdvancedLocation location = this.startSpawn.get((Integer) this.getPlayerProperties(p.getName(), "spawnIndex"));
                location.teleport(p);
            }
        } else if (this.getStartSpawn().size() == 1) {
            AdvancedLocation location = this.startSpawn.get(0);
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
        this.preStartPass = true;
    }

    public void sendMessageToAll(String string) {
        PlayerTools.sendMessage(players, string);
        PlayerTools.sendMessage(spectators, string);
    }

    public void sendActionbarToAll(String string) {
        PlayerTools.sendActionbar(players, string);
        PlayerTools.sendActionbar(spectators, string);
    }

    public void sendTitleToAll(String string) {
        PlayerTools.sendTitle(players, string);
        PlayerTools.sendTitle(spectators, string);
    }

    public void sendTipToAll(String string) {
        PlayerTools.sendTip(players, string);
        PlayerTools.sendTip(spectators, string);
    }

    public void sendMessageToAll(Language language, String string, Object... params) {
        PlayerTools.sendMessage(players, language, string, params);
        PlayerTools.sendMessage(spectators, language, string, params);
    }

    public void sendActionbarToAll(Language language, String string, Object... params) {
        PlayerTools.sendActionbar(players, language, string, params);
        PlayerTools.sendActionbar(spectators, language, string, params);
    }

    public void sendTitleToAll(Language language, String string, Object... params) {
        PlayerTools.sendTitle(players, language, string, params);
        PlayerTools.sendTitle(spectators, language, string, params);
    }

    public void sendTipToAll(Language language, String string, Object... params) {
        PlayerTools.sendTip(players, language, string, params);
        PlayerTools.sendTip(spectators, language, string, params);
    }

    public void addPlayLevel(Level loadLevel) {
        playLevels.add(loadLevel);
    }

    public void removePlayLevel(Level loadLevel) {
        playLevels.remove(loadLevel);
    }
}
